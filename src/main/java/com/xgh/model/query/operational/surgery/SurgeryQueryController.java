package com.xgh.model.query.operational.surgery;

import com.xgh.infra.controller.BasicQueryController;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/surgeries")
public class SurgeryQueryController extends BasicQueryController<Surgery, SurgeryQueryService> {
	@Autowired
	protected SurgeryQueryController(SurgeryQueryService service) {
		super(service);
	}

	@GetMapping("/{year}/{month}")
	protected List<LocalDate> findDatesWithSurgeryByMonth(@PathVariable Integer year, @PathVariable Integer month) {
		return service.findDatesWithAppointmentByMonth(LocalDate.of(year, month, 1));
	}

	@GetMapping("/{year}/{month}/{day}")
	protected List<Surgery> findByDate(@PathVariable Integer year, @PathVariable Integer month,
			@PathVariable Integer day) {
		return service.findByDate(LocalDate.of(year, month, day));
	}
}
