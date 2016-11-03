import com.dependencymgr.processor.InstallationType;
import com.dependencymgr.processor.SystemDependencyMgr;
import com.dependencymgr.processor.SystemDependencyMgrImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by raghuram gururajan on 11/2/16.
 */

public class SystemDependencyTest {
    SystemDependencyMgr systemDependencyMgr =null;
    @Before
    public void setUp() throws Exception {
        systemDependencyMgr = new SystemDependencyMgrImpl();
    }

    @Test
    public void addDependencyComponentsAndVerify() {
        //Estabish dependency of component a on component b,componentc
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTA COMPONENTB COMPONENTC");
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTB COMPONENTD");
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTC COMPONENTE COMPONENTF");
        List<String> dependenciesCompA = systemDependencyMgr.getDependency("COMPONENTA");
        assertTrue(dependenciesCompA.contains("COMPONENTB"));
        assertTrue(dependenciesCompA.contains("COMPONENTC"));
        List<String> dependenciesCompB = systemDependencyMgr.getDependency("COMPONENTB");
        assertTrue(dependenciesCompA.contains("COMPONENTD"));
        List<String> dependenciesCompC = systemDependencyMgr.getDependency("COMPONENTC");
        assertTrue(dependenciesCompA.contains("COMPONENTE"));
        assertTrue(dependenciesCompA.contains("COMPONENTF"));

    }

    @Test
    public void removeDependencyComponentsAndVerify() {
        //Estabish dependency of component a on component b,componentc
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTA COMPONENTB COMPONENTC");
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTB COMPONENTD");
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTC COMPONENTE COMPONENTF");
        List<String> dependenciesCompA = systemDependencyMgr.getDependency("COMPONENTA");
        assertTrue(dependenciesCompA.contains("COMPONENTB"));
        assertTrue(dependenciesCompA.contains("COMPONENTC"));
        systemDependencyMgr.removeComponentWithDependency("COMPONENTA");
        dependenciesCompA = systemDependencyMgr.getDependency("COMPONENTA");
        assertTrue(dependenciesCompA.size() == 0);

    }

    @Test
    public void installComponentsAndDependenciesVerify() {
        //Estabish dependency of component a on component b,componentc
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTA COMPONENTB COMPONENTC");
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTB COMPONENTD");
        systemDependencyMgr.processCommand("DEPENDENCY COMPONENTC COMPONENTE COMPONENTF");
        systemDependencyMgr.processCommand("INSTALL COMPONENTA");
        assertTrue(systemDependencyMgr.verifyInstalledComponent("COMPONENTA" , InstallationType.EXPLICIT));
        assertTrue(systemDependencyMgr.verifyInstalledComponent("COMPONENTB" , InstallationType.IMPLICIT));
        assertTrue(systemDependencyMgr.verifyInstalledComponent("COMPONENTC" , InstallationType.IMPLICIT));
        //ComponentB dependends on ComponentD  so its implicity installed
        assertTrue(systemDependencyMgr.verifyInstalledComponent("COMPONENTD" , InstallationType.IMPLICIT));
        //ComponentC dependends on ComponentE,ComponentF  so its implicity installed
        assertTrue(systemDependencyMgr.verifyInstalledComponent("COMPONENTE" , InstallationType.IMPLICIT));
        assertTrue(systemDependencyMgr.verifyInstalledComponent("COMPONENTF" , InstallationType.IMPLICIT));



    }


    @After
    public void tearDown() throws Exception {
        systemDependencyMgr= null;
    }
}
