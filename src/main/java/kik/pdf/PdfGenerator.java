package kik.pdf;

import com.lowagie.text.DocumentException;
import kik.booking.data.Booking;
import kik.booking.data.BookingRepository;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;


import java.io.*;
import java.time.LocalDate;
import java.util.Comparator;


/**
 * Controller for PDF Generation
 *
 * @author Jonas Höpner
 * @version 0.1.0
 */

@Controller
public class PdfGenerator {
    //for PDF creation
    @Autowired
    private TemplateEngine templateEngine;

	private BookingRepository repository;

	/**
	 * standard constructor used by Spring
	 * @param repository the {@link BookingRepository}
	 */
	public PdfGenerator(BookingRepository repository) {
		this.repository = repository;
	}

	/**
	 * Creates a PDF out of the {@link Event}s in a {@link Booking}
	 * @param id the ID of the {@link Booking}
	 * @return an application/pdf
	 */
	@PreAuthorize("hasRole('ORGA')")
    @GetMapping(path = "/generatePDF/")
	public ResponseEntity<byte[]> pdfController(@RequestParam Long id) {
		if(!repository.existsById(id)) {
			System.err.println("This Booking does not exist!");
			return null;
		}
		return createPdf(null, repository.findById(id).get());
	}

	/**
	 * the PDF generator itself
	 * @param templateName the Template to be used for generation, defaults to pdf/generatePDF.html
	 * @param booking the {@link Booking} to be used
	 * @return an application/pdf
	 */
    private ResponseEntity<byte[]>  createPdf(String templateName, Booking booking) {
        if (templateName == null) {
        	templateName = "pdf/generatePDF";
		}

        //setting some variables
		float percentage = booking.getConditions().getPercentage() / 100f;
		Long reducedCardSum = 0l;
		Long normalCardSum = 0l;
		double nettoSum = 0d;
		double bruttoSum = 0d;
		double sumInvoice = 0f;
		float MwSt = 0f;
		float MwStBruttoNetto = 0f;
		float spio = (booking.getEvents().size() >= 2 ? booking.getConditions().getSpio() : 0); //only for more than 2ev

		//calculating number of cards of all events
        for (MovieEvent e : booking.getEvents()) {
        	reducedCardSum += e.getTickets().getReducedCount();
        	normalCardSum += e.getTickets().getNormalCount();
		}
		//calculating proceeds
		for (MovieEvent e : booking.getEvents()) {
			nettoSum += e.getTickets().getNormalSum()/*e.getTickets().getNormalPrice()*/;
			nettoSum += e.getTickets().getReducedSum()/*e.getTickets().getReducedPrice()*/;
		}

        bruttoSum = (100f/107f) * nettoSum; // round error here
		bruttoSum = round(bruttoSum,2);
		MwStBruttoNetto = (float)round(nettoSum - bruttoSum,2);


		// calculations final sum.
		// if the proceeds * booking.conditions.percentage is smaller than the minimumGuarantee, we have to take
		//	the minimum Guarantee.
		// else we calculate the proceeds * percentage, add the MwSt and print that.
        sumInvoice =
			booking.getConditions().getMinimumGuarantee() > bruttoSum * percentage ?
			booking.getConditions().getMinimumGuarantee() : bruttoSum * percentage;

        sumInvoice = sumInvoice +
			booking.getConditions().getAdvertisement() +
			booking.getConditions().getFreight() +
			booking.getConditions().getOther() +
			spio;

        MwSt = (float)round(sumInvoice * 0.07d,2);
		sumInvoice *= 1.07f; // +7%MwSt
		sumInvoice = round(sumInvoice,2);


		var entries = booking.getEvents();
		entries.sort(Comparator.comparing(MovieEvent::getDate));

        //generate PDF
        Context ctx = new Context();
        ctx.setVariable("entries", entries);
        ctx.setVariable("booking", booking);
        ctx.setVariable("bruttoSum", bruttoSum);
		ctx.setVariable("nettoSum", nettoSum);
		ctx.setVariable("normalCardSum", normalCardSum);
		ctx.setVariable("reducedCardSum", reducedCardSum);
		ctx.setVariable("sumInvoice", sumInvoice);
		ctx.setVariable("MwSt",MwSt);
		ctx.setVariable("MwStBruttoNetto",MwStBruttoNetto);
		ctx.setVariable("spio",spio);



		String processedHtml = templateEngine.process(templateName, ctx);
        FileOutputStream os = null;

		File file = null;
		try {
			file = File.createTempFile(LocalDate.now().toString() + "_"
				+ booking.getMovie().getGermanName(),".pdf");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
            os = new FileOutputStream(file);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) { System.err.println("couldn't save pdf, fileout Error"); }
            }
        }

        //build response to send pdf to the user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(file.getName(), file.getName());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0\"");
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
		byte[] contents = new byte[0];
		try {
			contents = IOUtils.toByteArray(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.exists()) {
			file.delete(); //clear our footprint in $TMP
		}
        //return pdf to download
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);

    }


	/**
	 * rounds a double to given number of decimalPoints
	 * @param value double to be rounded
	 * @param decimalPoints number of points after comma
	 * @return rounded value
	 */
	private double round(double value, int decimalPoints) {
		double d = Math.pow(10, decimalPoints);
		return Math.round(value * d) / d;
	}

}
