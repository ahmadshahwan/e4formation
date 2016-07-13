package com.opcoach.training.rental.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;


/**
 * The activator class controls the plug-in life cycle
 */
public class RentalUIActivator implements  BundleActivator, RentalUIConstants
{

	
	// The plug-in ID
	public static final String PLUGIN_ID = "com.opcoach.training.rental.ui";

	// The shared instance
	private static RentalUIActivator plugin;

	/** The map of possible color providers (read in extensions) */
	private Map<String, Palette> paletteManager = new HashMap<String, Palette>();

	private IPreferenceStore preferenceStore;

	/**
	 * The constructor
	 */
	public RentalUIActivator()
	{
		System.out.println("Start Rental UI Activator");  // test
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		plugin = this;
		System.out.println("Start rental ui bundle");
		readViewExtensions();
		readColorProviderExtensions();
		
		// Init image reg
		ImageRegistry reg = null;
		Bundle e4Bundel = Platform.getBundle("org.eclipse.e4.ui.workbench");
		if (e4Bundel != null) {
			BundleContext osgiContext = e4Bundel.getBundleContext();
			IEclipseContext e4Context = EclipseContextFactory.getServiceContext(osgiContext);
			reg = e4Context.get(ImageRegistry.class);
			if (reg == null) {
				reg = new ImageRegistry();
				e4Context.set(ImageRegistry.class, reg);
			}
			initializeImageRegistry(reg);
		}
		// initializeFontRegistry();
				
	}


/*	private void initializeFontRegistry()
	{
		FontData[] fdc = new FontData[] { new FontData("times",12,SWT.NORMAL), new FontData("times",12,SWT.ITALIC), new FontData("times",14,SWT.BOLD)};
		FontData[] fdr = new FontData[] { new FontData("courier",12,SWT.NORMAL), new FontData("courier",12,SWT.ITALIC), new FontData("courier",14,SWT.BOLD)};
		FontRegistry fr = JFaceResources.getFontRegistry();
		fr.put(FONT_CUSTOMER, fdc);
		fr.put(FONT_RENTAL_OBJECT, fdr);
		
		
	}
*/
	
	public void readColorProviderExtensions()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extp = reg.getExtensionPoint("com.opcoach.training.rental.ui.Palette");
		IExtension[] extensions = extp.getExtensions();

		for (IExtension ext : extensions)
		{
			IConfigurationElement[] config = ext.getConfigurationElements();
			for (IConfigurationElement elt : config)
			{
				// Create the palette for current extension.
				try
				{
					// Create the executable extension
					IColorProvider delegatedICP = (IColorProvider) elt.createExecutableExtension("paletteClass");
					
					// Add it (with its name) in the color provider map
					Palette p = new Palette(elt.getAttribute("id"));
					p.setName(elt.getAttribute("name"));
					p.setColorProvider(delegatedICP);
					paletteManager.put(p.getId(), p);
					
					// paletteManager.put(elt.getAttribute("name"), delegatedICP);
				} catch (CoreException e)
				{
					IStatus st = new Status(IStatus.ERROR, PLUGIN_ID, "Impossible de creer la classe de palette : "+
				                  elt.getAttribute("paletteClass"),e);
					// E34 voir les log
					// getLog().log(st);
					
				}
			}
		}

	}

	
	public void readViewExtensions()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
	
			for (IConfigurationElement elt : reg.getConfigurationElementsFor("org.eclipse.ui.views"))
			{
				if (elt.getName().equals("view"))
					System.out.println("Plugin : " + elt.getNamespaceIdentifier() + "\t\t\tVue : " + elt.getAttribute("name"));
			}

	}

	/* @return a never null collection of overriden color providers */

	public Map<String, Palette> getPaletteManager()
	{
		return paletteManager;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RentalUIActivator getDefault()
	{
		return plugin;
	}


	protected void initializeImageRegistry(ImageRegistry reg)
	{
		Bundle b = FrameworkUtil.getBundle(getClass());

		// Then fill the values...
		reg.put(IMG_CUSTOMER, ImageDescriptor.createFromURL(b.getEntry(IMG_CUSTOMER)));
		reg.put(IMG_RENTAL, ImageDescriptor.createFromURL(b.getEntry(IMG_RENTAL)));
		reg.put(IMG_RENTAL_OBJECT, ImageDescriptor.createFromURL(b.getEntry(IMG_RENTAL_OBJECT)));
		reg.put(IMG_AGENCY, ImageDescriptor.createFromURL(b.getEntry(IMG_AGENCY)));
		reg.put(IMG_COLLAPSE_ALL, ImageDescriptor.createFromURL(b.getEntry(IMG_COLLAPSE_ALL)));
		reg.put(IMG_EXPAND_ALL, ImageDescriptor.createFromURL(b.getEntry(IMG_EXPAND_ALL)));

	}
	
    public IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
			preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, FrameworkUtil.getBundle(this.getClass()).getSymbolicName());

        }
        return preferenceStore;
    }
	
}
