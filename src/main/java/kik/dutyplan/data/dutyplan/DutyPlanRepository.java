package kik.dutyplan.data.dutyplan;

import kik.dutyplan.data.dutyplan.DutyPlan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 * Repository for {@link DutyPlan}s
 */
public interface DutyPlanRepository extends CrudRepository<DutyPlan, Long> {
	Streamable<DutyPlan> findAll();
}
