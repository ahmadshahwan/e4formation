package com.opcoach.training.rental.ui.e4.commands;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.opcoach.training.rental.Customer;


public class CopyCustomer
{
	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {
		return o instanceof Customer;
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) Customer c, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, EventBroker broker) 
	{
		MessageDialog.openInformation(shell,
				"Copy Client",
				"Copying this customer : " + (c.getDisplayName()));
		
		
		Clipboard clipboard = new Clipboard(Display.getCurrent());
		String textData = c.getDisplayName();
		String rtfData = "{\\rtf1\\b\\i " + textData + "}";
		Transfer[] transfers = new Transfer[]{TextTransfer.getInstance(), RTFTransfer.getInstance()};
		Object[] data = new Object[] {textData, rtfData};
		clipboard.setContents(data, transfers);
		clipboard.dispose();
		
		broker.post("agency/copy", c);
	}

}
