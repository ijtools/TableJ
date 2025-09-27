/**
 * 
 */
package ijt.table.gui;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * Manages the plugins loaded by the application. This concerns the built-in
 * plugins embedded with the application, as well as user plugins that can be
 * added with the "plugins" directory.
 */
public class PluginManager
{
    // ===================================================================
    // Management of Singleton
    
    private static PluginManager instance = null;
    
    public static final PluginManager getInstance()
    {
        if (instance == null)
        {
            instance = new PluginManager();
        }
        return instance;
    }

    // ===================================================================
    // Class variables

    /**
     * A list of plugins that have been loaded.
     */
    Map<Class<?>, FramePlugin> plugins = new HashMap<>();

    
    // ===================================================================
    // Constructor
    
    /**
     * Private constructor to ensure it is called only once, from the
     * {@code getInstance()} method.
     */
    private PluginManager()
    {
    }
    

    // ===================================================================
    // General methods

    /**
     * Returns an instance of the plugin associated to the specified class. If
     * the plugin has already been loaded, it is returned. Otherwise, a new
     * FramePlugin is created, using a strategy that depends on the class:
     * <ul>
     * <li>if the class implements {@code FramePlugin}, calls the empty
     * constructor</li>
     * <li>if the class implements {@code ArrayOperator}, calls the empty
     * constructor and encapsulated the operator within an instance of
     * {@code ImageArrayOperatorPlugin}</li>
     * <li>if the class implements {@code ImageOperator}, calls the empty
     * constructor and encapsulates the operator within an instance of {@code
     * ImageOperatorPlugin}</li>
     * </ul>
     * If the plugin could not be created or loaded, return null, and prints the
     * encountered error messages on the error stream.
     * 
     * @param itemClass
     *            the class of the item used to retrieve or create a plugin
     * @return an instance of the plugin from the specified class
     */
    @SuppressWarnings("unchecked")
    public FramePlugin retrievePlugin(Class<?> itemClass)
    {
        FramePlugin plugin = plugins.get(itemClass);
        if (plugin != null)
        {
            return plugin;
        }
        // If there was already an attempt to create the plugin, do not try again 
        if (plugins.containsKey(itemClass))
        {
            return null;
        }

        try
        {
            if (FramePlugin.class.isAssignableFrom(itemClass))
            {
                // most common case: the class corresponds to an implementation
                // of FramePlugin
                // simply try to instantiate a new plugin from the constructor
                plugin = createPluginInstance((Class<? extends FramePlugin>) itemClass);
            }
            else
            {
                System.err.println("Could not find how to create plugin for item with class: " + itemClass.getName());
            }
        }
        catch (FramePluginInstantiationException ex)
        {
            // If the plugin could not be instantiated, displays the error,
            // but do not break the application flow.
            // Returns a "null" plugin that will be ignored by the GUI builder.
            ex.printStackTrace(System.err);
            plugin = null;
        }

        plugins.put(itemClass, plugin);
        return plugin;
    }

    private FramePlugin createPluginInstance(Class<? extends FramePlugin> pluginClass) throws FramePluginInstantiationException
    {
        Constructor<? extends FramePlugin> cons;
        try
        {
            // retrieve empty constructor of the plugin
            cons = pluginClass.getConstructor();
        }
        catch (Exception ex)
        {
            throw new FramePluginInstantiationException("Could not create constructor for Plugin: " + pluginClass.getName(), ex);
        }

        try
        {
            // Instantiate a new plugin from the constructor
            return (FramePlugin) cons.newInstance();
        }
        catch (Exception ex)
        {
            throw new FramePluginInstantiationException("Could not instantiate Plugin: " + pluginClass.getName(), ex);
        }
    }


    // ===================================================================
    // General methods

}
