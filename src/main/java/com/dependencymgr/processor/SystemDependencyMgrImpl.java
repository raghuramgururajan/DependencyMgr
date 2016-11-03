package com.dependencymgr.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by raghuram gururajan on 11/02/16.
 * Class that implements dependency manager operations and maintains list of dependency graph along with installed set of components
 */
public class SystemDependencyMgrImpl implements SystemDependencyMgr {


    //Dependency graph to store the component and list of dependencies
    public Map<String, List<String>> dependencyGraph;
    //Map to store the list of installed components
    public Map<String, InstallationType> installedComponents;

    public SystemDependencyMgrImpl() {
        dependencyGraph = new HashMap<String, List<String>>();
        installedComponents = new HashMap<String, InstallationType>();
    }

    /**
     * Method to add dependency to the dependency graph
     *
     * @param componentName
     * @param dependency
     */
    public void addDependency(String componentName, String dependency) {
        //Validation to make sure we are adding right component
        if (componentName == null || componentName.length() == 0) {
            return;
        }
        //Validation to make sure that a component is not added to itself as dependency
        if (componentName.equals(dependency)) {
            return;
        }
        List<String> dependencies = new ArrayList<String>();
        if (dependencyGraph.containsKey(componentName)) {
            dependencies = dependencyGraph.get(componentName);
        }
        if (dependency != null && !dependencies.contains(dependency))
            dependencies.add(dependency);
        dependencyGraph.put(componentName, dependencies);
    }

    /**
     * Method to get dependency based on a component name
     *
     * @param componentName
     * @return list of dependency components
     */
    public List<String> getDependency(String componentName) {
        List<String> componentDependenciesList = new ArrayList<String>();

        if (dependencyGraph.containsKey(componentName)) {
            List<String> dependentComponents = dependencyGraph.get(componentName);
            for (String dependentComp : dependentComponents) {
                List<String> dependentCompDependency = getDependency(dependentComp);
                if (dependentComp != null) {
                    componentDependenciesList.addAll(dependentCompDependency);
                }
            }

            componentDependenciesList.add(componentName);
        }
        return componentDependenciesList;
    }

    /**
     * Method to install dependency
     *
     * @param componentName
     * @return boolean indicating if the installation is sucessfull or not
     */
    private boolean installDependency(String componentName) {
        //This list returns all the dependent components for the componentName along with the componentName currently being invoked
        //For example Depends BROWSER TCPIP HTML ,getDependency(browser) will return tcpip,html
        List<String> allDependencies = getDependency(componentName);
        //Add current component also to be installed with its dependencies
        allDependencies.add(componentName);
        boolean isInstalled = false;
        for (String dependentComp : allDependencies) {
            if (!installedComponents.containsKey(dependentComp)) {
                System.out.println("Installing " + dependentComp);
                //The component we are not directly installing is a implicit dependency
                if (!dependentComp.equals(componentName))
                    installedComponents.put(dependentComp, InstallationType.IMPLICIT);
                else
                    //The component we are directly installing is a explicit dependency
                    installedComponents.put(dependentComp, InstallationType.EXPLICIT);
                isInstalled = true;
            }

        }

        return isInstalled;

    }

    public boolean verifyInstalledComponent(String componentName, InstallationType type) {
        if (componentName == null || componentName.length() == 0) {
            return false;
        }
        if (installedComponents.containsKey(componentName) && installedComponents.get(componentName) == type) {
            return true;
        }
        return false;
    }

    /**
     * Method to remove dependency
     *
     * @param componentName
     * @return boolean to indicate if dependency was removed
     */
    public boolean removeComponentWithDependency(String componentName) {

        List<List<String>> chainOfDependencies = new ArrayList<List<String>>();
        if (dependencyGraph.containsKey(componentName)) {
            chainOfDependencies = new ArrayList<List<String>>(dependencyGraph.values());
            for (List<String> dependencyComp : chainOfDependencies) {
                if (dependencyComp.contains(componentName)) {
                    return false;
                }
            }

            System.out.println("Removing " + componentName);
            List<String> allDependentComp = getDependency(componentName);
            dependencyGraph.remove(componentName);
            installedComponents.remove(componentName);
            boolean isAllChildDependenciesRemoved = true;
            for (String comp : allDependentComp) {
                if (installedComponents.containsKey(comp)) {
                    InstallationType dependency = installedComponents.get(comp);
                    if (dependency == InstallationType.EXPLICIT) {
                        continue;
                    }
                }
                removeComponentWithDependency(comp);
            }


            return true;
        }
        return false;
    }


    /**
     * Method to list components
     */
    public void listComponents() {
        List<String> sortedKeys = new ArrayList<String>(installedComponents.size());
        sortedKeys.addAll(installedComponents.keySet());
        Collections.sort(sortedKeys);
        for (String installedComp : sortedKeys) {
            System.out.println(installedComp);

        }
    }


    /**
     * Main method to invoke the program and it takes in a argument which is the file name
     *
     * @param args
     */
    public static void main(String[] args) {
        SystemDependencyMgrImpl sd = new SystemDependencyMgrImpl();
        if (args.length == 0) {
            System.out.println("Please input a full qualified input file name like /User/test/temp1.txt");
            return;
        }

        // Construct BufferedReader from FileReader
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(args[0])));
            String line = null;
            while ((line = br.readLine()) != null) {
                sd.processCommand(line);
            }
            br.close();
        } catch (IOException ex) {
            System.out.println("Exception occured while reading input " + ex.getMessage());
        }

    }

    /**
     * Method to process the input commands from file line by line
     *
     * @param line
     */
    public void processCommand(String line) {
        line = line.toUpperCase();
        System.out.println(line);
        if (line.length() == 0) {
            return;
        }
        String[] commandTokens = line.split(" ");
        String mainComponent = null;
        for (int i = 0; i < commandTokens.length; i++) {
            if (commandTokens[i].equals(" ") || commandTokens[i].length() == 0) {
                continue;
            }
            if (line.contains("LIST")) {
                listComponents();
            }
            if (i > 0) {
                if (line.contains("DEPEND")) {
                    //Assign the first component as the main component to which other dependencies are related
                    if (mainComponent == null && commandTokens[i] != null) {
                        mainComponent = commandTokens[i];
                        continue;
                    } else if (i < commandTokens.length && mainComponent != null) {
                        addDependency(mainComponent, commandTokens[i]);
                        //Make sure that every component is added to dependency graph though if its not having any dependency
                        addDependency(commandTokens[i], null);
                    }


                } else if (line.contains("INSTALL")) {
                    //Make sure that all components that are installed have entry in dependency graph
                    if (!dependencyGraph.containsKey(commandTokens[i])) {
                        addDependency(commandTokens[i], null);
                    }
                    boolean result = installDependency(commandTokens[i]);
                    if (!result) {
                        System.out.println(commandTokens[i] + " is already installed.");
                    }
                } else if (line.contains("REMOVE")) {
                    if (!installedComponents.containsKey(commandTokens[i])) {
                        System.out.println(commandTokens[i] + "is not installed");

                    } else {
                        boolean isRemoved = removeComponentWithDependency(commandTokens[i]);
                        if (!isRemoved) {
                            System.out.println(commandTokens[i] + " is still needed.");

                        }
                    }
                }

            }
        }

    }


}
