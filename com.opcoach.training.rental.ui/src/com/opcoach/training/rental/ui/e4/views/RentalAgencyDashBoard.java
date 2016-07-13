package com.opcoach.training.rental.ui.e4.views;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.opcoach.training.rental.Customer;
import com.opcoach.training.rental.Rental;
import com.opcoach.training.rental.RentalAgency;
import com.opcoach.training.rental.RentalObject;
import com.opcoach.training.rental.core.RentalCoreActivator;
import com.opcoach.training.rental.ui.RentalUIConstants;

public class RentalAgencyDashBoard 
{
	public static final String VIEW_ID = "com.opcoach.rental.ui.agencyDashboard";

	private TableViewer objectViewer;
	private TableViewer rentalViewer;
	private TableViewer customerViewer;
	private CustomerComparator comparator;

	private Viewer focusedViewer;
	
	private Shell activeShell;

	@Inject
	public RentalAgencyDashBoard(MPart p, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
		p.setElementId(VIEW_ID);
		this.activeShell = shell;
		// TODO Auto-generated constructor stub
	}
	
	@Inject
	private ESelectionService selectionService;
	
	@Inject
	private ImageRegistry imgReg;

	@PostConstruct
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));
		Composite top = new Composite(parent, SWT.BORDER);
		top.setLayout(new GridLayout(2, true));
		top.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		Composite bottom = new Composite(parent, SWT.BORDER);
		bottom.setLayout(new GridLayout(1, true));
		bottom.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		RentalAgency currentAgency = RentalCoreActivator.getAgency();

		// Create the 2 title lables in top.
		Label cTitle = new Label(top, SWT.NONE);
		cTitle.setLayoutData(new GridData(SWT.CENTER));
		cTitle.setText("Customers");

		Label cObjects = new Label(top, SWT.NONE);
		cObjects.setLayoutData(new GridData(SWT.CENTER));
		cObjects.setText("Objects");

		customerViewer = createCustomerTable(top, currentAgency);
		objectViewer = createObjectTable(top, currentAgency);
		rentalViewer = createRentalTable(bottom, currentAgency);

		// The current view is now selection provider to choose the focused
		// viewer
		
//		ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
//			
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
		// getSite().setSelectionProvider(this);

	}

	private TableViewer createCustomerTable(Composite top, RentalAgency currentAgency)
	{
		// Create the customer table with 2 columns: firstname and name
		TableViewer viewer = new TableViewer(top);
		final Table cTable = viewer.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		GridData gd_cTable = new GridData(SWT.FILL);
		gd_cTable.verticalAlignment = SWT.TOP;
		cTable.setLayoutData(gd_cTable);

		// Create the first column for firstname
		TableViewerColumn firstNameCol = new TableViewerColumn(viewer, SWT.CENTER);
		firstNameCol.getColumn().setWidth(200);
		firstNameCol.getColumn().setText("Firstname");
		firstNameCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					return ((Customer) element).getFirstName();
				}

				@Override
				public Image getImage(Object element)
				{
					Image i = imgReg.get(RentalUIConstants.IMG_CUSTOMER);
					return i;
				}

				@Override
				public String getToolTipText(Object element)
				{
					return "Information about : " + ((Customer) element).getDisplayName();
				}
			});
		//ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
		firstNameCol.getColumn().setImage(imgReg.get(RentalUIConstants.IMG_CUSTOMER));
		firstNameCol.getColumn().addSelectionListener(getHeaderSelectionAdapter(viewer, firstNameCol.getColumn(), 0));
		// Add the sort stuff to manage clic on header and the customerComparator
		comparator = new CustomerComparator();
		viewer.setComparator(comparator);
		

		// Create the second column for name
		TableViewerColumn nameCol = new TableViewerColumn(viewer, SWT.NONE);
		nameCol.getColumn().setWidth(200);
		nameCol.getColumn().setText("LastName");
		nameCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					return ((Customer) element).getLastName();
				}
			});
		nameCol.getColumn().addSelectionListener(getHeaderSelectionAdapter(viewer, nameCol.getColumn(), 1));

		// Set input data and content provider (default ArrayContentProvider)
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(currentAgency.getCustomers());

		// Manage the focus when entering the view
		setFocusOnViewer(viewer);

		
		// Add two menus : one on table, and anoter one on header.
		final Menu tableMenu = new Menu(viewer.getTable());
		viewer.getTable().setMenu(tableMenu);
		createMenuItem(tableMenu, firstNameCol.getColumn());
		createMenuItem(tableMenu, nameCol.getColumn());

		final Menu headerMenu = new Menu(this.activeShell, SWT.POP_UP);
		createMenuItem(headerMenu, firstNameCol.getColumn());

		final Table table = viewer.getTable();
		table.addListener(SWT.MenuDetect, new Listener()
			{
				public void handleEvent(Event event)
				{
					Point pt = Display.getCurrent().map(null, table, new Point(event.x, event.y));
					Rectangle clientArea = table.getClientArea();
					boolean header = clientArea.y <= pt.y && pt.y < (clientArea.y + table.getHeaderHeight());
					table.setMenu(header ? headerMenu : tableMenu);
				}
			});

		/*
		 * IMPORTANT: Dispose the menus (only the current menu, set with
		 * setMenu(), will be automatically disposed)
		 */
		table.addListener(SWT.Dispose, new Listener()
			{
				public void handleEvent(Event event)
				{
					headerMenu.dispose();
					tableMenu.dispose();
				}
			});

		provideSelection(viewer);
		return viewer;
	}

	private SelectionAdapter getHeaderSelectionAdapter(final TableViewer viewer, final TableColumn column,
		      final int index) {
		    SelectionAdapter selectionAdapter = new SelectionAdapter() {
		      @Override
		      public void widgetSelected(SelectionEvent e) {
		        comparator.setColumn(index);
		        int dir = comparator.getDirection();
		        viewer.getTable().setSortDirection(dir);
		        viewer.getTable().setSortColumn(column);
		        viewer.refresh();
		      }
		    };
		    return selectionAdapter;
		  }
	private void createMenuItem(Menu parent, final TableColumn column)
	{
		final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
		itemName.setText(column.getText());
		itemName.setSelection(column.getResizable());
		itemName.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if (itemName.getSelection())
					{
						column.setWidth(150);
						column.setResizable(true);
					} else
					{
						column.setWidth(0);
						column.setResizable(false);
					}
				}
			});
	}

	private TableViewer createObjectTable(Composite top, RentalAgency currentAgency)
	{
		// Create the object table with 1 column : name
		TableViewer viewer = new TableViewer(top); // , SWT.MULTI | SWT.H_SCROLL
													// | SWT.V_SCROLL |
													// SWT.FULL_SELECTION |
													// SWT.BORDER);
		final Table cTable = viewer.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		GridData gd_cTable = new GridData(SWT.FILL);
		gd_cTable.verticalAlignment = SWT.TOP;
		cTable.setLayoutData(gd_cTable);

		TableViewerColumn nameCol = new TableViewerColumn(viewer, SWT.NONE);
		nameCol.getColumn().setWidth(100);
		nameCol.getColumn().setText("Name");
		nameCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					return ((RentalObject) element).getName();
				}
			});

		// Set input data and content provider (default ArrayContentProvider)
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(currentAgency.getObjectsToRent());

		// Set menu manager and selection provider on viewer (can be removed)
		setFocusOnViewer(viewer);

		provideSelection(viewer);
		return viewer;
	}
	
	private TableViewer createRentalTable(Composite top, RentalAgency currentAgency)
	{
		// Create the rental table with 2 columns: firstname and name
		TableViewer viewer = new TableViewer(top);
		final Table cTable = viewer.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		cTable.setLayoutData(new GridData(SWT.FILL));

		TableViewerColumn objectCol = new TableViewerColumn(viewer, SWT.NONE);
		objectCol.getColumn().setWidth(150);
		objectCol.getColumn().setText("Object");
		objectCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					return ((Rental) element).getRentedObject().getName();
				}
			});
		TableViewerColumn customerCol = new TableViewerColumn(viewer, SWT.NONE);
		customerCol.getColumn().setWidth(150);
		customerCol.getColumn().setText("Customer");
		customerCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					return ((Rental) element).getCustomer().getDisplayName();
				}
			});

		TableViewerColumn startDateCol = new TableViewerColumn(viewer, SWT.NONE);
		startDateCol.getColumn().setWidth(150);
		startDateCol.getColumn().setText("Start Date");
		startDateCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					return df.format(((Rental) element).getStartDate());
				}
			});

		TableViewerColumn endDateCol = new TableViewerColumn(viewer, SWT.NONE);
		endDateCol.getColumn().setWidth(150);
		endDateCol.getColumn().setText("End Date");
		endDateCol.setLabelProvider(new ColumnLabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					return df.format(((Rental) element).getEndDate());
				}
			});

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(currentAgency.getRentals());

		provideSelection(viewer);
		// Set menu manager and selection provider on viewer (can be removed)
		setFocusOnViewer(viewer);
		return viewer;
	}

	private void provideSelection(Viewer agencyViewer) {
		agencyViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				selectionService.setSelection(sel.size() == 1 ? sel.getFirstElement() : sel.toArray());				
			}
		});
	}

	private void setFocusOnViewer(final TableViewer v)
	{
		// viewer has a popup
		/*
		 * MenuManager menuManager = new MenuManager(); Menu menu =
		 * menuManager.createContextMenu(v.getControl());
		 * v.getControl().setMenu(menu);
		 * getSite().registerContextMenu(menuManager, v);
		 */
		// Must listen to the focus of control to react and set the
		// corresponding selectionProvider
		v.getControl().addFocusListener(new FocusListener()
			{

				@Override
				public void focusGained(FocusEvent e)
				{
					focusedViewer = v;
				}

				@Override
				public void focusLost(FocusEvent e)
				{
				}

			});

	}

	// E34 revoire preference store lisener
//	@Override
//	public void init(IViewSite site) throws PartInitException
//	{
//		super.init(site);
//		// On s'enregistre en tant que pref listener sur le preference store...
//		RentalUIActivator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
//
//		// This part is now selection listener to refresh selection from the
//		// agency view
//		getSite().getPage().addSelectionListener(this);
//
//	}
//
//	@Override
//	public void dispose()
//	{
//		RentalUIActivator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
//
//		getSite().getPage().removeSelectionListener(this);
//		super.dispose();
//	}
//
//	@Override
//	public void propertyChange(PropertyChangeEvent event)
//	{
//		customerViewer.refresh();
//		objectViewer.refresh();
//		rentalViewer.refresh();
//	}

	@Focus
	public void setFocus()
	{
		customerViewer.getControl().setFocus();
	}
	
	@Inject @Optional
	public void selectionChanged(@Named(IServiceConstants.ACTIVE_SELECTION) Customer c, @Named(IServiceConstants.ACTIVE_PART) MPart p) {
		manageSelection(customerViewer, c, p);
	}
	
	@Inject @Optional
	public void selectionChanged(@Named(IServiceConstants.ACTIVE_SELECTION) Rental r, @Named(IServiceConstants.ACTIVE_PART) MPart p) {
		if (r != null) {
			manageSelection(rentalViewer, r, p);
			manageSelection(customerViewer, r.getCustomer(), p);
			manageSelection(objectViewer, r.getRentedObject(), p);
		}
	}
	
	@Inject @Optional
	public void selectionChanged(@Named(IServiceConstants.ACTIVE_SELECTION) RentalObject o, @Named(IServiceConstants.ACTIVE_PART) MPart p) {
		manageSelection(objectViewer, o, p);
	}
	
	private void manageSelection(Viewer v, Object obj, MPart part) {
		if (v == null || obj == null || part == null) return;
		if (!VIEW_ID.equals(part.getElementId())) {
			v.setSelection(new StructuredSelection(obj));
		}
	}

//	@Override
//	public void addSelectionChangedListener(ISelectionChangedListener listener)
//	{
//		customerViewer.addSelectionChangedListener(listener);
//		objectViewer.addSelectionChangedListener(listener);
//		rentalViewer.addSelectionChangedListener(listener);
//	}

//	@Override
//	public ISelection getSelection()
//	{
//		return (focusedViewer != null) ? focusedViewer.getSelection() : null;
//	}

	// E34 fait par l injection
//	@Override
//	public void removeSelectionChangedListener(ISelectionChangedListener listener)
//	{
//		customerViewer.removeSelectionChangedListener(listener);
//		objectViewer.removeSelectionChangedListener(listener);
//		rentalViewer.removeSelectionChangedListener(listener);
//	}

//	@Override
//	public void setSelection(ISelection selection)
//	{
//		focusedViewer.setSelection(selection);
//	}
//
//
//	@Override
//	public void selectionChanged(IWorkbenchPart part, ISelection selection)
//	{
//		if (part != this)
//		{
//			customerViewer.setSelection(selection, true);
//			objectViewer.setSelection(selection, true);
//			rentalViewer.setSelection(selection, true);
//		}
//
//	}

	/** A customer comparator for the table, dealing with column index */
	public class CustomerComparator extends ViewerComparator {
		  private int columnIndex;
		  private static final int DESCENDING = 1;
		  private int direction = DESCENDING;

		  public CustomerComparator() {
		    this.columnIndex = 0;
		    direction = DESCENDING;
		  }

		  public int getDirection() {
		    return direction == 1 ? SWT.DOWN : SWT.UP;
		  }

		  /** Called when click on table header */
		  public void setColumn(int column) {
		    if (column == this.columnIndex) {
		      // Same column as last sort; toggle the direction
		      direction = 1 - direction;
		    } else {
		      // New column; do an ascending sort
		      this.columnIndex = column;
		      direction = DESCENDING;
		    }
		  }

		  @Override
		  public int compare(Viewer viewer, Object e1, Object e2) {
		    Customer p1 = (Customer) e1;
		    Customer p2 = (Customer) e2;
		    int rc = 0;
		    switch (columnIndex) {
		    case 0:
		      rc = p1.getFirstName().compareTo(p2.getFirstName());
		      break;
		    case 1:
		      rc = p1.getLastName().compareTo(p2.getLastName());
		      break;
		    default:
		      rc = 0;
		    }
		    // If descending order, flip the direction
		    if (direction == DESCENDING) {
		      rc = -rc;
		    }
		    return rc;
		  }

		} 

	

}
