package spoon.smpl;

/**
 * OperationFilter defines categories for prioritization and ordering of operations.
 *
 * A class implementing Operation contains a method accept() that takes a value of
 * OperationFilter. The Transformer provides calls to these Operations with OperationFilter
 * values for the Operations to decide for themselves whether or not to act on the call.
 *
 * The current set defines the following order of application:
 * 1) PREPEND operations, in stored order.
 * 2) APPEND operations, in reverse stored order.
 * 3) DELETE operations, in stored order.
 */
public enum OperationFilter {
    PREPEND, APPEND, DELETE;
}
