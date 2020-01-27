package kik.dutyplan.data.job;

import kik.user.data.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface JobRepository extends CrudRepository<Job, Long> {

	@Override
	Streamable<Job> findAll();

	Streamable<Job> findByWorker(User worker);
}
