# DependencyMgr
Sample project demonstrating dependency handling across different software components .The code demonstrates functionality of adding dependencies,listing dependencies,listing components and removing dependencies/components
Steps to run the project
========================
The project is built using maven and it can be imported as a maven project
a)Import the project as a maven project in eclipse or Intellij
b)Navigate to the root folder of the project and run the Test file com.dependencymgr.processor.SystemDependencyTest
  The test can be run from the IDE or using the maven command maven clean test
   mvn clean -Dtest=com.dependencymgr.processor.SystemDependencyTest test
c)The main classes for dependency management are  SystemDependencyMgr(Interface) that defines methods for adding,removing,listing dependencies
  and the implementation class SystemDependencyMgrImpl
   
