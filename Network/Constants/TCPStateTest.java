package Network.Constants;

/**
 * Comprehensive unit tests for TCPState enum
 * Testing framework: Simple Java testing (following project patterns)
 * 
 * This test suite validates the TCPState enum which represents
 * the various states in the TCP connection lifecycle according to RFC 793.
 */
public class TCPStateTest {

    public static void main(String[] args) {
        System.out.println("Running TCPState enum tests...");
        
        try {
            testAllTCPStatesExist();
            testEnumCount();
            testClosedStateProperties();
            testSynSentStateProperties();
            testSynReceivedStateProperties();
            testEstablishedStateProperties();
            testFinWait1StateProperties();
            testFinWait2StateProperties();
            testTimeWaitStateProperties();
            testValueOfForAllStates();
            testValueOfWithInvalidInput();
            testValueOfWithNullInput();
            testOrdinalOrdering();
            testEqualityComparison();
            testHashCodeConsistency();
            testToStringRepresentation();
            testSwitchStatementCompatibility();
            testTCPStateTransitionCompatibility();
            testSerializationCompatibility();
            testEnumValidation();
            testConnectionEstablishmentStates();
            testConnectionTerminationStates();
            testCompareToFunctionality();
            testArrayOperations();
            testEdgeCasesAndBoundaryConditions();
            testPerformanceCharacteristics();
            
            System.out.println("All TCPState tests passed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testAllTCPStatesExist() {
        TCPState[] expectedStates = {
            TCPState.CLOSED,
            TCPState.SYN_SENT,
            TCPState.SYN_RECEIVED,
            TCPState.ESTABLISHED,
            TCPState.FIN_WAIT_1,
            TCPState.FIN_WAIT_2,
            TCPState.TIME_WAIT
        };
        
        TCPState[] actualStates = TCPState.values();
        
        assert actualStates.length == 7 : "Should have exactly 7 TCP states";
        
        for (int i = 0; i < expectedStates.length; i++) {
            assert actualStates[i] == expectedStates[i] : 
                "State at position " + i + " should be " + expectedStates[i];
        }
        
        for (TCPState expectedState : expectedStates) {
            assert expectedState != null : "TCP state should not be null";
        }
        
        System.out.println("✓ testAllTCPStatesExist passed");
    }

    private static void testEnumCount() {
        assert TCPState.values().length == 7 : "TCPState should have exactly 7 values";
        System.out.println("✓ testEnumCount passed");
    }

    private static void testClosedStateProperties() {
        assert "CLOSED".equals(TCPState.CLOSED.name()) : "CLOSED name should be 'CLOSED'";
        assert TCPState.CLOSED.ordinal() == 0 : "CLOSED ordinal should be 0";
        System.out.println("✓ testClosedStateProperties passed");
    }

    private static void testSynSentStateProperties() {
        assert "SYN_SENT".equals(TCPState.SYN_SENT.name()) : "SYN_SENT name should be 'SYN_SENT'";
        assert TCPState.SYN_SENT.ordinal() == 1 : "SYN_SENT ordinal should be 1";
        System.out.println("✓ testSynSentStateProperties passed");
    }

    private static void testSynReceivedStateProperties() {
        assert "SYN_RECEIVED".equals(TCPState.SYN_RECEIVED.name()) : "SYN_RECEIVED name should be 'SYN_RECEIVED'";
        assert TCPState.SYN_RECEIVED.ordinal() == 2 : "SYN_RECEIVED ordinal should be 2";
        System.out.println("✓ testSynReceivedStateProperties passed");
    }

    private static void testEstablishedStateProperties() {
        assert "ESTABLISHED".equals(TCPState.ESTABLISHED.name()) : "ESTABLISHED name should be 'ESTABLISHED'";
        assert TCPState.ESTABLISHED.ordinal() == 3 : "ESTABLISHED ordinal should be 3";
        System.out.println("✓ testEstablishedStateProperties passed");
    }

    private static void testFinWait1StateProperties() {
        assert "FIN_WAIT_1".equals(TCPState.FIN_WAIT_1.name()) : "FIN_WAIT_1 name should be 'FIN_WAIT_1'";
        assert TCPState.FIN_WAIT_1.ordinal() == 4 : "FIN_WAIT_1 ordinal should be 4";
        System.out.println("✓ testFinWait1StateProperties passed");
    }

    private static void testFinWait2StateProperties() {
        assert "FIN_WAIT_2".equals(TCPState.FIN_WAIT_2.name()) : "FIN_WAIT_2 name should be 'FIN_WAIT_2'";
        assert TCPState.FIN_WAIT_2.ordinal() == 5 : "FIN_WAIT_2 ordinal should be 5";
        System.out.println("✓ testFinWait2StateProperties passed");
    }

    private static void testTimeWaitStateProperties() {
        assert "TIME_WAIT".equals(TCPState.TIME_WAIT.name()) : "TIME_WAIT name should be 'TIME_WAIT'";
        assert TCPState.TIME_WAIT.ordinal() == 6 : "TIME_WAIT ordinal should be 6";
        System.out.println("✓ testTimeWaitStateProperties passed");
    }

    private static void testValueOfForAllStates() {
        for (TCPState state : TCPState.values()) {
            assert state == TCPState.valueOf(state.name()) : 
                "valueOf should return correct enum for " + state.name();
        }
        System.out.println("✓ testValueOfForAllStates passed");
    }

    private static void testValueOfWithInvalidInput() {
        String[] invalidInputs = {"INVALID_STATE", "closed", "LISTEN", "CLOSING", "LAST_ACK", "", " ", "SYN SENT"};
        
        for (String invalidInput : invalidInputs) {
            try {
                TCPState.valueOf(invalidInput);
                assert false : "Should throw IllegalArgumentException for " + invalidInput;
            } catch (IllegalArgumentException e) {
                // Expected behavior
            }
        }
        System.out.println("✓ testValueOfWithInvalidInput passed");
    }

    private static void testValueOfWithNullInput() {
        try {
            TCPState.valueOf(null);
            assert false : "Should throw NullPointerException for null input";
        } catch (NullPointerException e) {
            // Expected behavior
        }
        System.out.println("✓ testValueOfWithNullInput passed");
    }

    private static void testOrdinalOrdering() {
        assert TCPState.CLOSED.ordinal() < TCPState.SYN_SENT.ordinal() : "CLOSED < SYN_SENT";
        assert TCPState.SYN_SENT.ordinal() < TCPState.SYN_RECEIVED.ordinal() : "SYN_SENT < SYN_RECEIVED";
        assert TCPState.SYN_RECEIVED.ordinal() < TCPState.ESTABLISHED.ordinal() : "SYN_RECEIVED < ESTABLISHED";
        assert TCPState.ESTABLISHED.ordinal() < TCPState.FIN_WAIT_1.ordinal() : "ESTABLISHED < FIN_WAIT_1";
        assert TCPState.FIN_WAIT_1.ordinal() < TCPState.FIN_WAIT_2.ordinal() : "FIN_WAIT_1 < FIN_WAIT_2";
        assert TCPState.FIN_WAIT_2.ordinal() < TCPState.TIME_WAIT.ordinal() : "FIN_WAIT_2 < TIME_WAIT";
        System.out.println("✓ testOrdinalOrdering passed");
    }

    private static void testEqualityComparison() {
        assert TCPState.CLOSED.equals(TCPState.CLOSED) : "CLOSED should equal itself";
        assert TCPState.ESTABLISHED.equals(TCPState.ESTABLISHED) : "ESTABLISHED should equal itself";
        
        assert !TCPState.CLOSED.equals(TCPState.ESTABLISHED) : "CLOSED should not equal ESTABLISHED";
        assert !TCPState.SYN_SENT.equals(TCPState.SYN_RECEIVED) : "SYN_SENT should not equal SYN_RECEIVED";
        assert !TCPState.CLOSED.equals(null) : "CLOSED should not equal null";
        assert !TCPState.CLOSED.equals("CLOSED") : "CLOSED should not equal string";
        System.out.println("✓ testEqualityComparison passed");
    }

    private static void testHashCodeConsistency() {
        for (TCPState state : TCPState.values()) {
            assert state.hashCode() == state.hashCode() : 
                "HashCode should be consistent for " + state.name();
        }
        
        // Different states should have different hash codes (highly likely)
        assert TCPState.CLOSED.hashCode() != TCPState.ESTABLISHED.hashCode() : 
            "Different states should have different hash codes";
        System.out.println("✓ testHashCodeConsistency passed");
    }

    private static void testToStringRepresentation() {
        assert "CLOSED".equals(TCPState.CLOSED.toString()) : "CLOSED toString";
        assert "SYN_SENT".equals(TCPState.SYN_SENT.toString()) : "SYN_SENT toString";
        assert "SYN_RECEIVED".equals(TCPState.SYN_RECEIVED.toString()) : "SYN_RECEIVED toString";
        assert "ESTABLISHED".equals(TCPState.ESTABLISHED.toString()) : "ESTABLISHED toString";
        assert "FIN_WAIT_1".equals(TCPState.FIN_WAIT_1.toString()) : "FIN_WAIT_1 toString";
        assert "FIN_WAIT_2".equals(TCPState.FIN_WAIT_2.toString()) : "FIN_WAIT_2 toString";
        assert "TIME_WAIT".equals(TCPState.TIME_WAIT.toString()) : "TIME_WAIT toString";
        System.out.println("✓ testToStringRepresentation passed");
    }

    private static void testSwitchStatementCompatibility() {
        for (TCPState state : TCPState.values()) {
            String result = getStateDescription(state);
            assert result != null : "Switch should handle all enum values";
            assert !result.isEmpty() : "Switch result should not be empty";
        }
        System.out.println("✓ testSwitchStatementCompatibility passed");
    }

    private static void testTCPStateTransitionCompatibility() {
        // Test that states can be used in typical TCP state machine logic
        assert isInitialState(TCPState.CLOSED) : "CLOSED should be an initial state";
        assert isActiveState(TCPState.ESTABLISHED) : "ESTABLISHED should be an active state";
        assert isClosingState(TCPState.FIN_WAIT_1) : "FIN_WAIT_1 should be a closing state";
        assert isClosingState(TCPState.FIN_WAIT_2) : "FIN_WAIT_2 should be a closing state";
        assert isClosingState(TCPState.TIME_WAIT) : "TIME_WAIT should be a closing state";
        
        assert !isActiveState(TCPState.CLOSED) : "CLOSED should not be an active state";
        assert !isInitialState(TCPState.ESTABLISHED) : "ESTABLISHED should not be an initial state";
        System.out.println("✓ testTCPStateTransitionCompatibility passed");
    }

    private static void testSerializationCompatibility() {
        // Test that enum values can be converted to/from strings reliably
        for (TCPState state : TCPState.values()) {
            String serialized = state.name();
            TCPState deserialized = TCPState.valueOf(serialized);
            assert state == deserialized : 
                "Serialization roundtrip should preserve enum value for " + state.name();
        }
        System.out.println("✓ testSerializationCompatibility passed");
    }

    private static void testEnumValidation() {
        for (TCPState state : TCPState.values()) {
            assert state != null : "Enum constant should not be null";
            assert state.name() != null : "Enum name should not be null";
            assert !state.name().isEmpty() : "Enum name should not be empty";
            assert state.name().matches("[A-Z_0-9]+") : 
                "Enum name should follow Java naming conventions: " + state.name();
        }
        System.out.println("✓ testEnumValidation passed");
    }

    private static void testConnectionEstablishmentStates() {
        assert isConnectionEstablishmentState(TCPState.SYN_SENT) : "SYN_SENT is establishment state";
        assert isConnectionEstablishmentState(TCPState.SYN_RECEIVED) : "SYN_RECEIVED is establishment state";
        
        assert !isConnectionEstablishmentState(TCPState.CLOSED) : "CLOSED is not establishment state";
        assert !isConnectionEstablishmentState(TCPState.ESTABLISHED) : "ESTABLISHED is not establishment state";
        assert !isConnectionEstablishmentState(TCPState.FIN_WAIT_1) : "FIN_WAIT_1 is not establishment state";
        System.out.println("✓ testConnectionEstablishmentStates passed");
    }

    private static void testConnectionTerminationStates() {
        assert isConnectionTerminationState(TCPState.FIN_WAIT_1) : "FIN_WAIT_1 is termination state";
        assert isConnectionTerminationState(TCPState.FIN_WAIT_2) : "FIN_WAIT_2 is termination state";
        assert isConnectionTerminationState(TCPState.TIME_WAIT) : "TIME_WAIT is termination state";
        
        assert !isConnectionTerminationState(TCPState.CLOSED) : "CLOSED is not termination state";
        assert !isConnectionTerminationState(TCPState.SYN_SENT) : "SYN_SENT is not termination state";
        assert !isConnectionTerminationState(TCPState.ESTABLISHED) : "ESTABLISHED is not termination state";
        System.out.println("✓ testConnectionTerminationStates passed");
    }

    private static void testCompareToFunctionality() {
        // Test enum comparison based on ordinal values
        assert TCPState.CLOSED.compareTo(TCPState.SYN_SENT) < 0 : "CLOSED < SYN_SENT";
        assert TCPState.SYN_SENT.compareTo(TCPState.CLOSED) > 0 : "SYN_SENT > CLOSED";
        assert TCPState.ESTABLISHED.compareTo(TCPState.ESTABLISHED) == 0 : "ESTABLISHED == ESTABLISHED";
        
        assert TCPState.FIN_WAIT_1.compareTo(TCPState.TIME_WAIT) < 0 : "FIN_WAIT_1 < TIME_WAIT";
        assert TCPState.TIME_WAIT.compareTo(TCPState.FIN_WAIT_1) > 0 : "TIME_WAIT > FIN_WAIT_1";
        System.out.println("✓ testCompareToFunctionality passed");
    }

    private static void testArrayOperations() {
        TCPState[] states = TCPState.values();
        
        // Test array contains all states
        TCPState[] expectedStates = {
            TCPState.CLOSED, TCPState.SYN_SENT, TCPState.SYN_RECEIVED,
            TCPState.ESTABLISHED, TCPState.FIN_WAIT_1, TCPState.FIN_WAIT_2,
            TCPState.TIME_WAIT
        };
        
        for (TCPState expectedState : expectedStates) {
            boolean found = false;
            for (TCPState state : states) {
                if (state == expectedState) {
                    found = true;
                    break;
                }
            }
            assert found : "Array should contain " + expectedState.name();
        }
        
        // Test that modifying the array doesn't affect the original enum
        TCPState[] statesCopy = TCPState.values();
        statesCopy[0] = null; // This should not affect the original enum
        assert TCPState.values()[0] == TCPState.CLOSED : "Original enum should be immutable";
        
        System.out.println("✓ testArrayOperations passed");
    }

    private static void testEdgeCasesAndBoundaryConditions() {
        // Test ordinal bounds
        assert TCPState.CLOSED.ordinal() >= 0 : "First ordinal should be >= 0";
        assert TCPState.TIME_WAIT.ordinal() == TCPState.values().length - 1 : "Last ordinal should be length - 1";
        
        // Test state name boundaries
        for (TCPState state : TCPState.values()) {
            assert state.name().length() > 0 : "State name should not be empty";
            assert state.name().length() <= 20 : "State name should be reasonable length";
            assert !state.name().contains(" ") : "State name should not contain spaces";
        }
        
        // Test that each state appears exactly once
        for (int i = 0; i < TCPState.values().length; i++) {
            for (int j = i + 1; j < TCPState.values().length; j++) {
                assert TCPState.values()[i] != TCPState.values()[j] : 
                    "Each state should appear exactly once";
            }
        }
        
        System.out.println("✓ testEdgeCasesAndBoundaryConditions passed");
    }

    private static void testPerformanceCharacteristics() {
        // Test that enum operations are fast (basic performance validation)
        long startTime = System.nanoTime();
        
        // Perform many enum operations
        for (int i = 0; i < 10000; i++) {
            TCPState[] states = TCPState.values();
            for (TCPState state : states) {
                state.name();
                state.ordinal();
                state.toString();
            }
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // This should complete very quickly (under 100ms for 10k iterations)
        assert duration < 100_000_000 : "Enum operations should be fast";
        
        System.out.println("✓ testPerformanceCharacteristics passed");
    }

    // Helper methods for testing TCP state logic compatibility
    private static boolean isInitialState(TCPState state) {
        return state == TCPState.CLOSED;
    }

    private static boolean isActiveState(TCPState state) {
        return state == TCPState.ESTABLISHED;
    }

    private static boolean isClosingState(TCPState state) {
        return state == TCPState.FIN_WAIT_1 || 
               state == TCPState.FIN_WAIT_2 || 
               state == TCPState.TIME_WAIT;
    }

    private static boolean isConnectionEstablishmentState(TCPState state) {
        return state == TCPState.SYN_SENT || state == TCPState.SYN_RECEIVED;
    }

    private static boolean isConnectionTerminationState(TCPState state) {
        return state == TCPState.FIN_WAIT_1 || 
               state == TCPState.FIN_WAIT_2 || 
               state == TCPState.TIME_WAIT;
    }

    private static String getStateDescription(TCPState state) {
        return switch (state) {
            case CLOSED -> "Connection closed";
            case SYN_SENT -> "SYN sent, waiting for response";
            case SYN_RECEIVED -> "SYN received, waiting for ACK";
            case ESTABLISHED -> "Connection established";
            case FIN_WAIT_1 -> "FIN sent, waiting for ACK";
            case FIN_WAIT_2 -> "FIN ACKed, waiting for FIN";
            case TIME_WAIT -> "Waiting for timeout";
        };
    }
}