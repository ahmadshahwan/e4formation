 
package com.opcoach.training.rental.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import com.opcoach.training.rental.Customer;
import com.opcoach.training.rental.RentalAgency;
import com.opcoach.training.rental.helpers.RentalAgencyGenerator;

public class RentalAddon {

	@PostConstruct
	public void init(IEclipseContext ctx) {
		ctx.set(RentalAgency.class, RentalAgencyGenerator.createSampleAgency());
	}
	
	@Inject
	@Optional
	public void onCopyCustomer(@UIEventTopic("agency/copy") Customer c) {
		System.out.println("Customer copied " + c.toString());
	}

}
