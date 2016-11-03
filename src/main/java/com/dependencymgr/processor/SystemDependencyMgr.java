package com.dependencymgr.processor;

import java.util.List;

/**
 * Created by raghuram gururajan on 11/2/16.
 * Main interface that defines dependency management operations
 */
public interface SystemDependencyMgr {

    /**
     * Method to add dependency to a component
     * @param componentName
     * @param dependency
     */
    public void addDependency(String componentName, String dependency);

    /**
     * Method to retrieve dependency
     * @param componentName
     * @return
     */
    public List<String> getDependency(String componentName);

    /**
     * Method to remove component and its dependenciesl
     * @param componentName
     * @return
     */
    public boolean removeComponentWithDependency(String componentName);

    /**
     * Method to list all components;
     */
    public void listComponents();

    /**
     * Process commands for dependencies
     * @param line
     */
    public void processCommand(String line);

    /**
     *
     * @param componentName
     * @param type
     * @return
     */
    public boolean verifyInstalledComponent(String componentName, InstallationType type);

}
