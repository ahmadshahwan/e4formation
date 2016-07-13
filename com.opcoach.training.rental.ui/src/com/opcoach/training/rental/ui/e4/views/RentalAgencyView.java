package com.opcoach.training.rental.ui.e4.views;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.opcoach.training.rental.RentalAgency;
import com.opcoach.training.rental.core.RentalCoreActivator;
import com.opcoach.training.rental.helpers.RentalAgencyGenerator;
import com.opcoach.training.rental.ui.RentalUIConstants;
import com.opcoach.training.rental.ui.views.AgencyTreeDragSourceListener;
import com.opcoach.training.rental.ui.views.RentalProvider;

public class RentalAgencyView implements RentalUIConstants
{
	public static final String VIEW_ID = "com.opcoach.rental.ui.rentalagencyview";

	private TreeViewer agencyViewer;

	private RentalProvider provider;

	public RentalAgencyView()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Inject
	private ESelectionService selectionService;

	@PostConstruct
	public void createPartControl(Composite parent, ImageRegistry imgReg)
	{
		parent.setLayout(new GridLayout(1, false));

		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		Button expandAll = new Button(comp, SWT.FLAT);
		expandAll.setImage(imgReg.get(IMG_EXPAND_ALL));
		expandAll.setToolTipText("Expand agency tree");
		expandAll.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					agencyViewer.expandAll();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		Button collapseAll = new Button(comp, SWT.FLAT);
		collapseAll.setImage(imgReg.get(IMG_COLLAPSE_ALL));
		collapseAll.setToolTipText("Collapse agency tree");
		collapseAll.addSelectionListener(new SelectionListener()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					agencyViewer.collapseAll();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		agencyViewer = new TreeViewer(parent);
		agencyViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true));
		provider = new RentalProvider(imgReg);
		agencyViewer.setContentProvider(provider);
		agencyViewer.setLabelProvider(provider);

		Collection<RentalAgency> agencies = new ArrayList<RentalAgency>();
		agencies.add(RentalCoreActivator.getAgency());

		RentalAgency lyon = RentalAgencyGenerator.createSampleAgency();
		lyon.setName("Lyon");
		agencies.add(lyon);

		agencyViewer.setInput(agencies);

		// Association de la vue sur un contexte d'aide
		
		// E34 voir la gestion de help dans E4
//		PlatformUI.getWorkbench().getHelpSystem()
//				.setHelp(agencyViewer.getControl(), "com.opcoach.training.rental.ui.rentalContext");

		// Autorise le popup sur le treeviewer
		// E34 revoir la gestion du popup menu
//		MenuManager menuManager = new MenuManager();
//		Menu menu = menuManager.createContextMenu(agencyViewer.getControl());
//		agencyViewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuManager, agencyViewer);

		// L'arbre est draggable
		DragSource ds = new DragSource(agencyViewer.getControl(), DND.DROP_COPY);
		Transfer[] ts = new Transfer[] { TextTransfer.getInstance(), RTFTransfer.getInstance(), URLTransfer.getInstance() };
		ds.setTransfer(ts);
		AgencyTreeDragSourceListener treeListener = new AgencyTreeDragSourceListener(agencyViewer, imgReg);
		ds.addDragListener(treeListener);
		
		agencyViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				selectionService.setSelection(sel.size() == 1 ? sel.getFirstElement() : sel.toArray());				
			}
		});

	}

	// E34 revoir la gestion des listener sur le site et siur le preferene store
//	@Override
//	public void init(IViewSite site) throws PartInitException
//	{
//		super.init(site);
//		// On s'enregistre en tant que pref listener sur le preference store...
//		RentalUIActivator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
//
//		// This treeview is now selection listener to be synchronized with the
//		// dashboard.
//		getSite().getPage().addSelectionListener(this);
//
//	}
//
//	@Override
//	public void dispose()
//	{
//		RentalUIActivator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
//
//		// This treeview must remove the selection listener
//		getSite().getPage().removeSelectionListener(this);
//
//		super.dispose();
//	}

	// E34 revoir le listener sur les preferences
//	@Override
//	public void propertyChange(PropertyChangeEvent event)
//	{
//		provider.initPalette();
//		agencyViewer.refresh();
//	}

	@Focus
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	// E34 revoir la gestion de selection
//	@Override
//	public void selectionChanged(IWorkbenchPart part, ISelection selection)
//	{
//		// Must check if this selection is coming from this part or from another
//		// one.
//		if (part != this)
//			agencyViewer.setSelection(selection, true);
//
//	}

}
