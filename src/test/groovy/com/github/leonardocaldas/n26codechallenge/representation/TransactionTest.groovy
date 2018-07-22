package com.github.leonardocaldas.n26codechallenge.representation

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator

class TransactionTest extends Specification {

    @Shared
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "should get timestamp in seconds"() {
        given:
        def transaction = Transaction.builder()
                .timestamp(10003)
                .build()

        when:
        def timestampInSeconds = transaction.getTimestampInSeconds()

        then:
        timestampInSeconds == 10
    }

    def "should create valid transaction"() {
        when:
        def transaction = Transaction.builder()
                .amount(10.0)
                .timestamp(100000L)
                .build()

        then:
        validator.validate(transaction).isEmpty()
    }

    @Unroll
    def "should not create invalid transaction with #with"() {
        given:
        def transaction = Transaction.builder()
                .amount(amount)
                .timestamp(timestamp)
                .build()

        when:
        def errors = validator.validate(transaction)

        then:
        (++errors.iterator()).getPropertyPath().toString() == property

        where:
        property    | amount | timestamp | with
        "amount"    | null   | 100000L   | "null amount"
        "timestamp" | 10.0   | null      | "null timestamp"
    }
}
