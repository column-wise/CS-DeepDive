package Network.Constants;

/**
 * Comprehensive unit tests for HTTPMethodType enum.
 * Tests cover all enum values, edge cases, and standard enum behaviors.
 * Uses simple assertion-based testing following the project's testing pattern.
 */
public class HTTPMethodTypeTest {

    /**
     * Test that all expected HTTP methods are present in the enum.
     */
    public void testAllHttpMethodsPresent() {
        // Verify all standard HTTP methods exist
        HTTPMethodType[] methods = HTTPMethodType.values();
        
        boolean hasGet = false, hasPost = false, hasPut = false, hasDelete = false;
        boolean hasHead = false, hasOptions = false, hasTrace = false, hasConnect = false, hasPatch = false;
        
        for (HTTPMethodType method : methods) {
            switch (method) {
                case GET: hasGet = true; break;
                case POST: hasPost = true; break;
                case PUT: hasPut = true; break;
                case DELETE: hasDelete = true; break;
                case HEAD: hasHead = true; break;
                case OPTIONS: hasOptions = true; break;
                case TRACE: hasTrace = true; break;
                case CONNECT: hasConnect = true; break;
                case PATCH: hasPatch = true; break;
            }
        }
        
        assert hasGet : "GET method should be present";
        assert hasPost : "POST method should be present";
        assert hasPut : "PUT method should be present";
        assert hasDelete : "DELETE method should be present";
        assert hasHead : "HEAD method should be present";
        assert hasOptions : "OPTIONS method should be present";
        assert hasTrace : "TRACE method should be present";
        assert hasConnect : "CONNECT method should be present";
        assert hasPatch : "PATCH method should be present";
        
        System.out.println("All HTTP methods are present in the enum");
    }

    /**
     * Test that the enum contains exactly 9 HTTP method types.
     */
    public void testExactlyNineHttpMethods() {
        HTTPMethodType[] methods = HTTPMethodType.values();
        assert methods.length == 9 : "Expected exactly 9 HTTP method types, but found: " + methods.length;
        System.out.println("Enum contains exactly 9 HTTP method types");
    }

    /**
     * Test valueOf method with valid HTTP method names.
     */
    public void testValueOfWithValidNames() {
        String[] validMethods = {"GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "CONNECT", "PATCH"};
        
        for (String methodName : validMethods) {
            try {
                HTTPMethodType method = HTTPMethodType.valueOf(methodName);
                assert method != null : "valueOf should return non-null for valid method: " + methodName;
                assert method.name().equals(methodName) : "Method name should match input: " + methodName;
            } catch (Exception e) {
                assert false : "valueOf should not throw exception for valid method: " + methodName + ", but got: " + e.getMessage();
            }
        }
        System.out.println("valueOf works correctly with all valid method names");
    }

    /**
     * Test valueOf method with invalid HTTP method names.
     */
    public void testValueOfWithInvalidNames() {
        String[] invalidMethods = {"get", "post", "INVALID", "123", "", " ", "HTTP_GET", "null"};
        
        for (String invalidMethod : invalidMethods) {
            try {
                HTTPMethodType.valueOf(invalidMethod);
                assert false : "valueOf should throw IllegalArgumentException for invalid method: " + invalidMethod;
            } catch (IllegalArgumentException e) {
                // Expected behavior
                System.out.println("Correctly threw IllegalArgumentException for invalid method: " + invalidMethod);
            } catch (Exception e) {
                assert false : "valueOf should throw IllegalArgumentException, but got: " + e.getClass().getSimpleName();
            }
        }
        System.out.println("valueOf correctly rejects invalid method names");
    }

    /**
     * Test valueOf method with null input.
     */
    public void testValueOfWithNull() {
        try {
            HTTPMethodType.valueOf(null);
            assert false : "valueOf should throw NullPointerException for null input";
        } catch (NullPointerException e) {
            // Expected behavior
            System.out.println("Correctly threw NullPointerException for null input");
        } catch (Exception e) {
            assert false : "valueOf should throw NullPointerException for null, but got: " + e.getClass().getSimpleName();
        }
    }

    /**
     * Test that enum ordinal values are consistent and in expected order.
     */
    public void testEnumOrdinalValues() {
        assert HTTPMethodType.GET.ordinal() == 0 : "GET should have ordinal 0";
        assert HTTPMethodType.POST.ordinal() == 1 : "POST should have ordinal 1";
        assert HTTPMethodType.PUT.ordinal() == 2 : "PUT should have ordinal 2";
        assert HTTPMethodType.DELETE.ordinal() == 3 : "DELETE should have ordinal 3";
        assert HTTPMethodType.HEAD.ordinal() == 4 : "HEAD should have ordinal 4";
        assert HTTPMethodType.OPTIONS.ordinal() == 5 : "OPTIONS should have ordinal 5";
        assert HTTPMethodType.TRACE.ordinal() == 6 : "TRACE should have ordinal 6";
        assert HTTPMethodType.CONNECT.ordinal() == 7 : "CONNECT should have ordinal 7";
        assert HTTPMethodType.PATCH.ordinal() == 8 : "PATCH should have ordinal 8";
        System.out.println("Enum ordinal values are in correct order");
    }

    /**
     * Test equality and identity of enum values.
     */
    public void testEnumEquality() {
        HTTPMethodType get1 = HTTPMethodType.GET;
        HTTPMethodType get2 = HTTPMethodType.valueOf("GET");
        
        assert get1 == get2 : "Same enum values should be identical objects";
        assert get1.equals(get2) : "Same enum values should be equal";
        assert get1.hashCode() == get2.hashCode() : "Same enum values should have same hash code";
        
        assert HTTPMethodType.GET != HTTPMethodType.POST : "Different enum values should not be identical";
        assert !HTTPMethodType.GET.equals(HTTPMethodType.POST) : "Different enum values should not be equal";
        
        System.out.println("Enum equality and identity work correctly");
    }

    /**
     * Test that each enum value has a valid string representation.
     */
    public void testStringRepresentation() {
        for (HTTPMethodType method : HTTPMethodType.values()) {
            String name = method.name();
            assert name != null : "Method name should not be null for: " + method;
            assert !name.isEmpty() : "Method name should not be empty for: " + method;
            assert name.matches("[A-Z]+") : "Method name should contain only uppercase letters: " + name;
            
            String toString = method.toString();
            assert toString.equals(name) : "toString() should match name() for: " + method;
        }
        System.out.println("All enum values have valid string representations");
    }

    /**
     * Test that enum can be used in switch statements properly.
     */
    public void testSwitchStatementCompatibility() {
        for (HTTPMethodType method : HTTPMethodType.values()) {
            String description = getMethodDescription(method);
            assert description != null : "Switch statement should handle method: " + method;
            assert !description.isEmpty() : "Description should not be empty for method: " + method;
            assert !description.equals("Unknown method") : "All methods should have known descriptions: " + method;
        }
        System.out.println("Enum works correctly in switch statements");
    }

    /**
     * Test that enum works properly with collections.
     */
    public void testCollectionCompatibility() {
        java.util.Set<HTTPMethodType> methodSet = new java.util.HashSet<>();
        for (HTTPMethodType method : HTTPMethodType.values()) {
            methodSet.add(method);
        }
        
        assert methodSet.size() == 9 : "Set should contain all 9 unique methods";
        assert methodSet.contains(HTTPMethodType.GET) : "Set should contain GET";
        assert methodSet.contains(HTTPMethodType.POST) : "Set should contain POST";
        assert methodSet.contains(HTTPMethodType.PATCH) : "Set should contain PATCH";
        
        // Test with EnumSet
        java.util.Set<HTTPMethodType> enumSet = java.util.EnumSet.allOf(HTTPMethodType.class);
        assert enumSet.size() == 9 : "EnumSet should contain all 9 methods";
        
        for (HTTPMethodType method : HTTPMethodType.values()) {
            assert enumSet.contains(method) : "EnumSet should contain method: " + method;
        }
        
        System.out.println("Enum works correctly with collections");
    }

    /**
     * Test enum serialization compatibility (basic checks).
     */
    public void testSerializationCompatibility() {
        for (HTTPMethodType method : HTTPMethodType.values()) {
            // Test that enum can be converted to string and back
            String methodName = method.name();
            HTTPMethodType reconstructed = HTTPMethodType.valueOf(methodName);
            assert method == reconstructed : "Enum should survive string conversion: " + method;
        }
        System.out.println("Enum serialization compatibility verified");
    }

    /**
     * Test that method names follow HTTP specification conventions.
     */
    public void testHttpSpecificationCompliance() {
        // Test that all methods are uppercase (HTTP spec requirement)
        for (HTTPMethodType method : HTTPMethodType.values()) {
            String name = method.name();
            assert name.equals(name.toUpperCase()) : "HTTP method names should be uppercase: " + name;
        }
        
        // Test that all methods are valid according to HTTP specification
        String[] validHttpMethods = {"GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "CONNECT", "PATCH"};
        HTTPMethodType[] enumMethods = HTTPMethodType.values();
        
        for (HTTPMethodType enumMethod : enumMethods) {
            boolean isValid = false;
            for (String validMethod : validHttpMethods) {
                if (enumMethod.name().equals(validMethod)) {
                    isValid = true;
                    break;
                }
            }
            assert isValid : "Enum contains invalid HTTP method: " + enumMethod.name();
        }
        
        System.out.println("All HTTP methods comply with HTTP specification");
    }

    /**
     * Test edge cases and boundary conditions.
     */
    public void testEdgeCases() {
        // Test that values() returns a new array each time (defensive copying)
        HTTPMethodType[] methods1 = HTTPMethodType.values();
        HTTPMethodType[] methods2 = HTTPMethodType.values();
        assert methods1 != methods2 : "values() should return new array instances";
        assert methods1.length == methods2.length : "values() should return arrays of same length";
        
        // Test that modifying returned array doesn't affect enum
        HTTPMethodType originalFirst = methods1[0];
        methods1[0] = null;
        HTTPMethodType[] methods3 = HTTPMethodType.values();
        assert methods3[0] != null : "Modifying values() result should not affect enum";
        assert methods3[0] == originalFirst : "Original enum value should be preserved";
        
        // Test compareTo method (inherited from Enum)
        assert HTTPMethodType.GET.compareTo(HTTPMethodType.POST) < 0 : "GET should come before POST";
        assert HTTPMethodType.POST.compareTo(HTTPMethodType.GET) > 0 : "POST should come after GET";
        assert HTTPMethodType.GET.compareTo(HTTPMethodType.GET) == 0 : "GET should equal itself in comparison";
        
        System.out.println("Edge cases and boundary conditions handled correctly");
    }

    /**
     * Test that the enum can be used in practical HTTP scenarios.
     */
    public void testPracticalUsageScenarios() {
        // Test safe methods (should not change server state)
        HTTPMethodType[] safeMethods = {HTTPMethodType.GET, HTTPMethodType.HEAD, HTTPMethodType.OPTIONS, HTTPMethodType.TRACE};
        for (HTTPMethodType method : safeMethods) {
            assert isSafeMethod(method) : method + " should be considered a safe method";
        }
        
        // Test idempotent methods (can be called multiple times safely)
        HTTPMethodType[] idempotentMethods = {HTTPMethodType.GET, HTTPMethodType.HEAD, HTTPMethodType.PUT, 
                                             HTTPMethodType.DELETE, HTTPMethodType.OPTIONS, HTTPMethodType.TRACE};
        for (HTTPMethodType method : idempotentMethods) {
            assert isIdempotentMethod(method) : method + " should be considered idempotent";
        }
        
        // Test methods that typically have request bodies
        HTTPMethodType[] methodsWithBodies = {HTTPMethodType.POST, HTTPMethodType.PUT, HTTPMethodType.PATCH};
        for (HTTPMethodType method : methodsWithBodies) {
            assert canHaveRequestBody(method) : method + " should allow request bodies";
        }
        
        System.out.println("Practical usage scenarios work correctly");
    }

    /**
     * Test enum with different case variations (stress testing valueOf).
     */
    public void testCaseVariations() {
        String[] mixedCaseMethods = {"get", "Get", "gET", "post", "Post", "PUT", "put"};
        
        for (String mixedCase : mixedCaseMethods) {
            try {
                HTTPMethodType.valueOf(mixedCase);
                // Only uppercase should succeed
                assert mixedCase.equals(mixedCase.toUpperCase()) : 
                    "Only uppercase method names should be accepted: " + mixedCase;
            } catch (IllegalArgumentException e) {
                // Non-uppercase should fail
                assert !mixedCase.equals(mixedCase.toUpperCase()) : 
                    "Non-uppercase method names should be rejected: " + mixedCase;
            }
        }
        
        System.out.println("Case sensitivity works correctly");
    }

    /**
     * Run all tests in sequence.
     */
    public static void runAllTests() {
        HTTPMethodTypeTest test = new HTTPMethodTypeTest();
        int testCount = 0;
        int passedTests = 0;
        
        System.out.println("=== Running HTTPMethodType Enum Tests ===\n");
        
        try {
            test.testAllHttpMethodsPresent();
            testCount++; passedTests++;
            
            test.testExactlyNineHttpMethods();
            testCount++; passedTests++;
            
            test.testValueOfWithValidNames();
            testCount++; passedTests++;
            
            test.testValueOfWithInvalidNames();
            testCount++; passedTests++;
            
            test.testValueOfWithNull();
            testCount++; passedTests++;
            
            test.testEnumOrdinalValues();
            testCount++; passedTests++;
            
            test.testEnumEquality();
            testCount++; passedTests++;
            
            test.testStringRepresentation();
            testCount++; passedTests++;
            
            test.testSwitchStatementCompatibility();
            testCount++; passedTests++;
            
            test.testCollectionCompatibility();
            testCount++; passedTests++;
            
            test.testSerializationCompatibility();
            testCount++; passedTests++;
            
            test.testHttpSpecificationCompliance();
            testCount++; passedTests++;
            
            test.testEdgeCases();
            testCount++; passedTests++;
            
            test.testPracticalUsageScenarios();
            testCount++; passedTests++;
            
            test.testCaseVariations();
            testCount++; passedTests++;
            
            System.out.println("\n=== Test Results ===");
            System.out.println("Tests run: " + testCount);
            System.out.println("Tests passed: " + passedTests);
            System.out.println("Tests failed: " + (testCount - passedTests));
            System.out.println("\n✓ All HTTPMethodType tests passed successfully!");
            
        } catch (AssertionError e) {
            testCount++;
            System.err.println("\n✗ Test failed: " + e.getMessage());
            System.err.println("Tests run: " + testCount);
            System.err.println("Tests passed: " + passedTests);
            System.err.println("Tests failed: " + (testCount - passedTests));
            e.printStackTrace();
        } catch (Exception e) {
            testCount++;
            System.err.println("\n✗ Unexpected error during testing: " + e.getMessage());
            System.err.println("Tests run: " + testCount);
            System.err.println("Tests passed: " + passedTests);
            System.err.println("Tests failed: " + (testCount - passedTests));
            e.printStackTrace();
        }
    }

    /**
     * Helper method for testing switch statements.
     */
    private String getMethodDescription(HTTPMethodType method) {
        switch (method) {
            case GET:
                return "Retrieve data from server";
            case POST:
                return "Submit data to server";
            case PUT:
                return "Update or create resource";
            case DELETE:
                return "Remove resource from server";
            case HEAD:
                return "Get headers only (no body)";
            case OPTIONS:
                return "Check available methods and capabilities";
            case TRACE:
                return "Perform diagnostic trace of request path";
            case CONNECT:
                return "Establish tunnel connection";
            case PATCH:
                return "Apply partial modifications to resource";
            default:
                return "Unknown method";
        }
    }

    /**
     * Helper method to determine if an HTTP method is safe (doesn't change server state).
     */
    private boolean isSafeMethod(HTTPMethodType method) {
        switch (method) {
            case GET:
            case HEAD:
            case OPTIONS:
            case TRACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Helper method to determine if an HTTP method is idempotent.
     */
    private boolean isIdempotentMethod(HTTPMethodType method) {
        switch (method) {
            case GET:
            case HEAD:
            case PUT:
            case DELETE:
            case OPTIONS:
            case TRACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Helper method to determine if an HTTP method typically allows request bodies.
     */
    private boolean canHaveRequestBody(HTTPMethodType method) {
        switch (method) {
            case POST:
            case PUT:
            case PATCH:
                return true;
            case GET:
            case DELETE: // DELETE can have body but it's not recommended
                return true; // Being permissive here
            default:
                return false;
        }
    }

    /**
     * Main method to run tests if executed directly.
     * This follows the pattern used in the existing test files.
     */
    public static void main(String[] args) {
        runAllTests();
    }
}