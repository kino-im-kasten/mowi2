package kik.distributor.management;

import kik.distributor.data.*;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * provides some sample Distributors
 * @author Jonas Höpner
 * @version 0.2.0
 */
@Component
@Order(60)
public class DistributorInitializer implements DataInitializer {
	private DistributorRepository distributorRepository;
	private ContactPersonRepository contactPersonRepository;
	private DistributorManagement distributorManagement;

	private static final Logger LOG = LoggerFactory.getLogger(DistributorInitializer.class);

	public DistributorInitializer(DistributorRepository distributorRepository,
								  ContactPersonRepository contactPersonRepository,
								  DistributorManagement distributorManagement) {
		this.contactPersonRepository = contactPersonRepository;
		this.distributorRepository = distributorRepository;
		this.distributorManagement = distributorManagement;
	}

	@Override
	public void initialize() {
		if(distributorRepository.findAll().iterator().hasNext()){
			return;
		}

		try {
			Long id = distributorManagement.createDistributor(new DistributorForm(
				"Gesunde Säureminen e.V.",
				"Im Bau 23",
				"03534465181",
				"ceo@301plus.com",
				"45678"));
			distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
				"General Wilhelm Klink",
				"0384584351",
				"willy@pow24.de"),
				id);
			distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
				"Sergeant Hans Schulz",
				"234234234234",
				"s.schulz@hansweb.de")
				, id);
			distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
				"Colonel Robert Hogan",
				"023123123",
				"hogansheroes@rentaspy.com"),
				id);

			id = distributorManagement.createDistributor(new DistributorForm(
				"Fiedel GmbH",
				"An der Violine 2",
				"03534465181",
				"",
				""));
			distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
				"Corporal Louis LeBeau",
				"+42324234234",
				"cllb@mail.fr"),
				id);
			distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
				"Frau Helga",
				"0384584351",
				"helga@secretary.de"),
				id);

			id = distributorManagement.createDistributor(new DistributorForm(
				"Gustav Krebs",
				"Im Bau 23",
				"03534465181",
				"info@gkrebs.de",
				""));

		} catch (DistributorManagement.DuplicateException e) {
			LOG.error("A duplicate Exception has been thrown." +
				" Likely the database is now persistent and you have to remove this class.");
		}
		LOG.info("Created sample Distributors and assigned ContactPersons");
	}
}
