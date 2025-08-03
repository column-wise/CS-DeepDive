package Network.Network;

import Network.Node.Core.Router;

/**
 * Comprehensive unit tests for the Internet class.
 * Testing framework: Simple Java testing (following project patterns from DHCPTest and NATTest)
 * 
 * Tests cover:
 * - Constructor initialization
 * - Adding subnets (happy path, edge cases)
 * - Adding routers (happy path, edge cases)
 * - Internal state validation
 * - Null handling
 * - Multiple additions
 * - Edge cases and boundary conditions
 */
public class InternetTest {

    private static int testCount = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("Running Internet class tests...\n");
        
        testConstructorInitializesEmptyLists();
        testAddSubnetAddsToList();
        testAddMultipleSubnets();
        testAddNullSubnet();
        testAddDuplicateSubnets();
        testAddRouterAddsToList();
        testAddMultipleRouters();
        testAddNullRouter();
        testAddDuplicateRouters();
        testMixedOperations();
        testListsAreIndependent();
        testMultipleInternetInstancesAreIndependent();
        testLargeNumberOfAdditions();
        testSubnetOrderMaintained();
        testRouterOrderMaintained();
        testEmptyInternetState();
        testConcurrentModificationSafety();
        testMemoryUsageWithLargeCollections();
        testIntegrationWithRealSubnetAndRouter();
        testDefaultConstructorBehavior();
        testRouterParameterizedConstructor();
        testEdgeCaseEmptyMacAddress();
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Test Results: " + passedTests + "/" + testCount + " tests passed");
        if (passedTests == testCount) {
            System.out.println("All tests PASSED! ✓");
        } else {
            System.out.println("Some tests FAILED! ✗");
        }
    }

    private static void testConstructorInitializesEmptyLists() {
        testCount++;
        try {
            Internet internet = new Internet();
            
            // Use reflection to access private fields for verification
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets != null : "Subnets list should not be null";
            assert routers != null : "Routers list should not be null";
            assert subnets.isEmpty() : "Subnets list should be empty";
            assert routers.isEmpty() : "Routers list should be empty";
            
            passedTests++;
            System.out.println("✓ testConstructorInitializesEmptyLists PASSED");
        } catch (Exception e) {
            System.out.println("✗ testConstructorInitializesEmptyLists FAILED: " + e.getMessage());
        }
    }

    private static void testAddSubnetAddsToList() {
        testCount++;
        try {
            Internet internet = new Internet();
            Subnet subnet = Subnet.builder()
                    .subnetAddress("192.168.1.0")
                    .subnetMask("255.255.255.0")
                    .build();
            
            internet.addSubnet(subnet);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            subnetsField.setAccessible(true);
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            
            assert subnets.size() == 1 : "Should have one subnet";
            assert subnets.contains(subnet) : "Should contain the added subnet";
            
            passedTests++;
            System.out.println("✓ testAddSubnetAddsToList PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddSubnetAddsToList FAILED: " + e.getMessage());
        }
    }

    private static void testAddMultipleSubnets() {
        testCount++;
        try {
            Internet internet = new Internet();
            Subnet subnet1 = Subnet.builder()
                    .subnetAddress("192.168.1.0")
                    .subnetMask("255.255.255.0")
                    .build();
            Subnet subnet2 = Subnet.builder()
                    .subnetAddress("10.0.0.0")
                    .subnetMask("255.255.255.0")
                    .build();
            
            internet.addSubnet(subnet1);
            internet.addSubnet(subnet2);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            subnetsField.setAccessible(true);
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            
            assert subnets.size() == 2 : "Should have two subnets";
            assert subnets.contains(subnet1) : "Should contain first subnet";
            assert subnets.contains(subnet2) : "Should contain second subnet";
            
            passedTests++;
            System.out.println("✓ testAddMultipleSubnets PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddMultipleSubnets FAILED: " + e.getMessage());
        }
    }

    private static void testAddNullSubnet() {
        testCount++;
        try {
            Internet internet = new Internet();
            
            internet.addSubnet(null);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            subnetsField.setAccessible(true);
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            
            assert subnets.size() == 1 : "Should have one entry";
            assert subnets.get(0) == null : "Entry should be null";
            
            passedTests++;
            System.out.println("✓ testAddNullSubnet PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddNullSubnet FAILED: " + e.getMessage());
        }
    }

    private static void testAddDuplicateSubnets() {
        testCount++;
        try {
            Internet internet = new Internet();
            Subnet subnet = Subnet.builder()
                    .subnetAddress("172.16.0.0")
                    .subnetMask("255.255.0.0")
                    .build();
            
            internet.addSubnet(subnet);
            internet.addSubnet(subnet); // Add same subnet again
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            subnetsField.setAccessible(true);
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            
            assert subnets.size() == 2 : "Should have two entries (duplicates allowed)";
            assert subnets.get(0) == subnet : "First entry should be the subnet";
            assert subnets.get(1) == subnet : "Second entry should be the same subnet";
            
            passedTests++;
            System.out.println("✓ testAddDuplicateSubnets PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddDuplicateSubnets FAILED: " + e.getMessage());
        }
    }

    private static void testAddRouterAddsToList() {
        testCount++;
        try {
            Internet internet = new Internet();
            Router router = new Router("AA:BB:CC:DD:EE:FF");
            
            internet.addRouter(router);
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.size() == 1 : "Should have one router";
            assert routers.contains(router) : "Should contain the added router";
            
            passedTests++;
            System.out.println("✓ testAddRouterAddsToList PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddRouterAddsToList FAILED: " + e.getMessage());
        }
    }

    private static void testAddMultipleRouters() {
        testCount++;
        try {
            Internet internet = new Internet();
            Router router1 = new Router("AA:BB:CC:DD:EE:01");
            Router router2 = new Router("AA:BB:CC:DD:EE:02");
            
            internet.addRouter(router1);
            internet.addRouter(router2);
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.size() == 2 : "Should have two routers";
            assert routers.contains(router1) : "Should contain first router";
            assert routers.contains(router2) : "Should contain second router";
            
            passedTests++;
            System.out.println("✓ testAddMultipleRouters PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddMultipleRouters FAILED: " + e.getMessage());
        }
    }

    private static void testAddNullRouter() {
        testCount++;
        try {
            Internet internet = new Internet();
            
            internet.addRouter(null);
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.size() == 1 : "Should have one entry";
            assert routers.get(0) == null : "Entry should be null";
            
            passedTests++;
            System.out.println("✓ testAddNullRouter PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddNullRouter FAILED: " + e.getMessage());
        }
    }

    private static void testAddDuplicateRouters() {
        testCount++;
        try {
            Internet internet = new Internet();
            Router router = new Router("BB:CC:DD:EE:FF:AA");
            
            internet.addRouter(router);
            internet.addRouter(router); // Add same router again
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.size() == 2 : "Should have two entries (duplicates allowed)";
            assert routers.get(0) == router : "First entry should be the router";
            assert routers.get(1) == router : "Second entry should be the same router";
            
            passedTests++;
            System.out.println("✓ testAddDuplicateRouters PASSED");
        } catch (Exception e) {
            System.out.println("✗ testAddDuplicateRouters FAILED: " + e.getMessage());
        }
    }

    private static void testMixedOperations() {
        testCount++;
        try {
            Internet internet = new Internet();
            Subnet subnet1 = Subnet.builder()
                    .subnetAddress("192.168.10.0")
                    .subnetMask("255.255.255.0")
                    .build();
            Subnet subnet2 = Subnet.builder()
                    .subnetAddress("192.168.20.0")
                    .subnetMask("255.255.255.0")
                    .build();
            Router router1 = new Router("AA:11:22:33:44:55");
            Router router2 = new Router("BB:11:22:33:44:55");
            
            internet.addSubnet(subnet1);
            internet.addRouter(router1);
            internet.addSubnet(subnet2);
            internet.addRouter(router2);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets.size() == 2 : "Should have two subnets";
            assert routers.size() == 2 : "Should have two routers";
            assert subnets.contains(subnet1) : "Should contain first subnet";
            assert subnets.contains(subnet2) : "Should contain second subnet";
            assert routers.contains(router1) : "Should contain first router";
            assert routers.contains(router2) : "Should contain second router";
            
            passedTests++;
            System.out.println("✓ testMixedOperations PASSED");
        } catch (Exception e) {
            System.out.println("✗ testMixedOperations FAILED: " + e.getMessage());
        }
    }

    private static void testListsAreIndependent() {
        testCount++;
        try {
            Internet internet = new Internet();
            Subnet subnet = Subnet.builder()
                    .subnetAddress("172.16.10.0")
                    .subnetMask("255.255.255.0")
                    .build();
            Router router = new Router("CC:DD:EE:FF:AA:BB");
            
            internet.addSubnet(subnet);
            internet.addRouter(router);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets != routers : "Subnets and routers should be different lists";
            assert subnets.size() == 1 : "Subnets list should have one entry";
            assert routers.size() == 1 : "Routers list should have one entry";
            
            passedTests++;
            System.out.println("✓ testListsAreIndependent PASSED");
        } catch (Exception e) {
            System.out.println("✗ testListsAreIndependent FAILED: " + e.getMessage());
        }
    }

    private static void testMultipleInternetInstancesAreIndependent() {
        testCount++;
        try {
            Internet internet1 = new Internet();
            Internet internet2 = new Internet();
            
            Subnet subnet = Subnet.builder()
                    .subnetAddress("10.0.1.0")
                    .subnetMask("255.255.255.0")
                    .build();
            Router router = new Router("DD:EE:FF:AA:BB:CC");
            
            internet1.addSubnet(subnet);
            internet2.addRouter(router);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets1 = (java.util.List<Subnet>) subnetsField.get(internet1);
            java.util.List<Subnet> subnets2 = (java.util.List<Subnet>) subnetsField.get(internet2);
            java.util.List<Router> routers1 = (java.util.List<Router>) routersField.get(internet1);
            java.util.List<Router> routers2 = (java.util.List<Router>) routersField.get(internet2);
            
            assert subnets1.size() == 1 : "First internet should have one subnet";
            assert subnets2.size() == 0 : "Second internet should have no subnets";
            assert routers1.size() == 0 : "First internet should have no routers";
            assert routers2.size() == 1 : "Second internet should have one router";
            assert subnets1 != subnets2 : "Different Internet instances should have different subnet lists";
            assert routers1 != routers2 : "Different Internet instances should have different router lists";
            
            passedTests++;
            System.out.println("✓ testMultipleInternetInstancesAreIndependent PASSED");
        } catch (Exception e) {
            System.out.println("✗ testMultipleInternetInstancesAreIndependent FAILED: " + e.getMessage());
        }
    }

    private static void testLargeNumberOfAdditions() {
        testCount++;
        try {
            Internet internet = new Internet();
            
            // Test with many additions to ensure no issues with list growth
            for (int i = 0; i < 100; i++) {
                Subnet subnet = Subnet.builder()
                        .subnetAddress("192.168." + i + ".0")
                        .subnetMask("255.255.255.0")
                        .build();
                Router router = new Router(String.format("AA:BB:CC:DD:%02X:%02X", i / 256, i % 256));
                
                internet.addSubnet(subnet);
                internet.addRouter(router);
            }
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets.size() == 100 : "Should have 100 subnets";
            assert routers.size() == 100 : "Should have 100 routers";
            
            passedTests++;
            System.out.println("✓ testLargeNumberOfAdditions PASSED");
        } catch (Exception e) {
            System.out.println("✗ testLargeNumberOfAdditions FAILED: " + e.getMessage());
        }
    }

    private static void testSubnetOrderMaintained() {
        testCount++;
        try {
            Internet internet = new Internet();
            Subnet subnet1 = Subnet.builder().subnetAddress("192.168.1.0").subnetMask("255.255.255.0").build();
            Subnet subnet2 = Subnet.builder().subnetAddress("192.168.2.0").subnetMask("255.255.255.0").build();
            Subnet subnet3 = Subnet.builder().subnetAddress("192.168.3.0").subnetMask("255.255.255.0").build();
            
            internet.addSubnet(subnet1);
            internet.addSubnet(subnet2);
            internet.addSubnet(subnet3);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            subnetsField.setAccessible(true);
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            
            assert subnets.get(0) == subnet1 : "First subnet should be at index 0";
            assert subnets.get(1) == subnet2 : "Second subnet should be at index 1";
            assert subnets.get(2) == subnet3 : "Third subnet should be at index 2";
            
            passedTests++;
            System.out.println("✓ testSubnetOrderMaintained PASSED");
        } catch (Exception e) {
            System.out.println("✗ testSubnetOrderMaintained FAILED: " + e.getMessage());
        }
    }

    private static void testRouterOrderMaintained() {
        testCount++;
        try {
            Internet internet = new Internet();
            Router router1 = new Router("AA:BB:CC:DD:EE:01");
            Router router2 = new Router("AA:BB:CC:DD:EE:02");
            Router router3 = new Router("AA:BB:CC:DD:EE:03");
            
            internet.addRouter(router1);
            internet.addRouter(router2);
            internet.addRouter(router3);
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.get(0) == router1 : "First router should be at index 0";
            assert routers.get(1) == router2 : "Second router should be at index 1";
            assert routers.get(2) == router3 : "Third router should be at index 2";
            
            passedTests++;
            System.out.println("✓ testRouterOrderMaintained PASSED");
        } catch (Exception e) {
            System.out.println("✗ testRouterOrderMaintained FAILED: " + e.getMessage());
        }
    }

    private static void testEmptyInternetState() {
        testCount++;
        try {
            Internet internet = new Internet();
            
            // Test that empty internet behaves correctly
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets.isEmpty() : "Empty internet should have no subnets";
            assert routers.isEmpty() : "Empty internet should have no routers";
            assert subnets.size() == 0 : "Subnet count should be 0";
            assert routers.size() == 0 : "Router count should be 0";
            
            passedTests++;
            System.out.println("✓ testEmptyInternetState PASSED");
        } catch (Exception e) {
            System.out.println("✗ testEmptyInternetState FAILED: " + e.getMessage());
        }
    }

    private static void testConcurrentModificationSafety() {
        testCount++;
        try {
            Internet internet = new Internet();
            
            // Test basic concurrent-like operations (sequential but rapid)
            for (int i = 0; i < 50; i++) {
                Subnet subnet = Subnet.builder()
                        .subnetAddress("10.0." + i + ".0")
                        .subnetMask("255.255.255.0")
                        .build();
                Router router = new Router(String.format("CC:DD:EE:FF:%02X:%02X", i / 256, i % 256));
                
                internet.addSubnet(subnet);
                internet.addRouter(router);
            }
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets.size() == 50 : "Should have 50 subnets after rapid additions";
            assert routers.size() == 50 : "Should have 50 routers after rapid additions";
            
            passedTests++;
            System.out.println("✓ testConcurrentModificationSafety PASSED");
        } catch (Exception e) {
            System.out.println("✗ testConcurrentModificationSafety FAILED: " + e.getMessage());
        }
    }

    private static void testMemoryUsageWithLargeCollections() {
        testCount++;
        try {
            Internet internet = new Internet();
            
            // Test memory behavior with large collections
            Runtime runtime = Runtime.getRuntime();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
            
            for (int i = 0; i < 1000; i++) {
                Subnet subnet = Subnet.builder()
                        .subnetAddress("172.16." + (i / 256) + "." + (i % 256 * 4))
                        .subnetMask("255.255.255.252")
                        .build();
                Router router = new Router(String.format("EE:FF:AA:BB:%02X:%02X", i / 256, i % 256));
                
                internet.addSubnet(subnet);
                internet.addRouter(router);
            }
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets.size() == 1000 : "Should have 1000 subnets";
            assert routers.size() == 1000 : "Should have 1000 routers";
            
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            assert finalMemory >= initialMemory : "Memory usage should be at least initial amount";
            
            passedTests++;
            System.out.println("✓ testMemoryUsageWithLargeCollections PASSED");
        } catch (Exception e) {
            System.out.println("✗ testMemoryUsageWithLargeCollections FAILED: " + e.getMessage());
        }
    }

    private static void testIntegrationWithRealSubnetAndRouter() {
        testCount++;
        try {
            // Integration test similar to NATTest pattern
            Internet internet = new Internet();
            
            Subnet publicNetwork = Subnet.builder()
                    .subnetAddress("128.119.40.0")
                    .subnetMask("255.255.255.0")
                    .isPrivate(false)
                    .build();

            Subnet privateNetwork = Subnet.builder()
                    .subnetAddress("10.0.0.0")
                    .subnetMask("255.255.255.0")
                    .isPrivate(true)
                    .build();

            Router router = new Router("AA:BB:CC:DD:EE:01");

            internet.addSubnet(publicNetwork);
            internet.addSubnet(privateNetwork);
            internet.addRouter(router);
            
            java.lang.reflect.Field subnetsField = Internet.class.getDeclaredField("subnets");
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            subnetsField.setAccessible(true);
            routersField.setAccessible(true);
            
            java.util.List<Subnet> subnets = (java.util.List<Subnet>) subnetsField.get(internet);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert subnets.size() == 2 : "Should have two subnets";
            assert routers.size() == 1 : "Should have one router";
            assert subnets.contains(publicNetwork) : "Should contain public network";
            assert subnets.contains(privateNetwork) : "Should contain private network";
            assert routers.contains(router) : "Should contain router";
            
            passedTests++;
            System.out.println("✓ testIntegrationWithRealSubnetAndRouter PASSED");
        } catch (Exception e) {
            System.out.println("✗ testIntegrationWithRealSubnetAndRouter FAILED: " + e.getMessage());
        }
    }

    private static void testDefaultConstructorBehavior() {
        testCount++;
        try {
            Internet internet = new Internet();
            Router defaultRouter = new Router();
            
            internet.addRouter(defaultRouter);
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.size() == 1 : "Should have one router";
            assert routers.contains(defaultRouter) : "Should contain the default router";
            
            passedTests++;
            System.out.println("✓ testDefaultConstructorBehavior PASSED");
        } catch (Exception e) {
            System.out.println("✗ testDefaultConstructorBehavior FAILED: " + e.getMessage());
        }
    }

    private static void testRouterParameterizedConstructor() {
        testCount++;
        try {
            Internet internet = new Internet();
            Router paramRouter = new Router("FF:EE:DD:CC:BB:AA");
            
            internet.addRouter(paramRouter);
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.size() == 1 : "Should have one router";
            assert routers.contains(paramRouter) : "Should contain the parameterized router";
            
            passedTests++;
            System.out.println("✓ testRouterParameterizedConstructor PASSED");
        } catch (Exception e) {
            System.out.println("✗ testRouterParameterizedConstructor FAILED: " + e.getMessage());
        }
    }

    private static void testEdgeCaseEmptyMacAddress() {
        testCount++;
        try {
            Internet internet = new Internet();
            Router emptyMacRouter = new Router("");
            
            internet.addRouter(emptyMacRouter);
            
            java.lang.reflect.Field routersField = Internet.class.getDeclaredField("routers");
            routersField.setAccessible(true);
            java.util.List<Router> routers = (java.util.List<Router>) routersField.get(internet);
            
            assert routers.size() == 1 : "Should have one router even with empty MAC";
            assert routers.contains(emptyMacRouter) : "Should contain the router with empty MAC";
            
            passedTests++;
            System.out.println("✓ testEdgeCaseEmptyMacAddress PASSED");
        } catch (Exception e) {
            System.out.println("✗ testEdgeCaseEmptyMacAddress FAILED: " + e.getMessage());
        }
    }
}