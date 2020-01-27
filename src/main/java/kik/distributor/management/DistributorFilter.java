package kik.distributor.management;

import kik.distributor.data.Distributor;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Class holding a static method for filtering and paging of Distributors
 */
public  class DistributorFilter {
	private static final int PADDING = 10;
	/**
	 * Calculates the amount of {@link Distributor} and which {@link Distributor}s are to be
	 * shown on the index and then overview pages for{@link Distributor}s
	 *
	 * @param model             data attribute model of HTML5 page
	 * @param distributors            the list with currently available {@link Distributor}s which are not old
	 * @param currentDistCount current position in this list
	 * @param backwards         indicates the movement direction
	 * @param switched 			indicates whether the user switched direction
	 */
	public static void switchCount(Model model,
													 List<Distributor> distributors,
													 Integer currentDistCount,
													 Boolean backwards,
													 Boolean switched) {
		if (distributors.isEmpty()) {
			model.addAttribute("currentDistCount", 0);
			return;
		}

		if (currentDistCount == null) {
			currentDistCount = 0;
		}
		if (backwards == null) {
			backwards = false;
		}
		if (switched == null) {
			switched = false;
		}

		int listSize = distributors.size();
		int newDistCount = backwards ? currentDistCount - PADDING : currentDistCount + PADDING;

		if (switched && backwards) {
//			currentDistCount -= PADDING;
//			newDistCount -= PADDING;
		}  else if (switched) {
			currentDistCount += PADDING;
			newDistCount += PADDING;
		}

		if (newDistCount >= listSize) {
			int from = Math.max(0, listSize - PADDING);
			model.addAttribute("distributors", distributors.subList(from, listSize));
			newDistCount = from;
		} else if (newDistCount < 0) {
			model.addAttribute("distributors", distributors.subList(0, Math.min(PADDING, listSize)));
			newDistCount = 0;
		} else {
			if (backwards) {
				model.addAttribute("distributors", distributors.subList(newDistCount, currentDistCount));
			} else {
				model.addAttribute("distributors", distributors.subList(currentDistCount, newDistCount));
			}
		}

		model.addAttribute("currentDistCount", newDistCount);
	}
}
