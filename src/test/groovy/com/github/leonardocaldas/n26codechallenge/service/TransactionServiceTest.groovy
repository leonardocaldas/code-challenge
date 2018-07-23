package com.github.leonardocaldas.n26codechallenge.service

import com.github.leonardocaldas.n26codechallenge.exceptions.TransactionOutOfRangeException
import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository
import com.github.leonardocaldas.n26codechallenge.representation.Transaction
import com.github.leonardocaldas.n26codechallenge.service.impl.TransactionServiceImpl
import spock.lang.Specification

import java.time.Instant
import java.util.function.UnaryOperator

class TransactionServiceTest extends Specification {

    def repositoy = Mock(TransactionAggregateRepository)
    def TIME_RANGE = 60L
    def service = new TransactionServiceImpl(repositoy, TIME_RANGE)

    def "should save new transaction"() {
        given:
        def currentMillis = Instant.now().toEpochMilli()
        def transaction = Transaction.builder()
                .amount(10.0)
                .timestamp(currentMillis)
                .build()

        def key = getIndexFromTransaction(transaction)

        def expectedAggregate = TransactionAggregate.builder()
                .max(transaction.getAmount())
                .min(transaction.getAmount())
                .sum(transaction.getAmount())
                .count(1L)
                .timestampInSeconds(transaction.getTimestampInSeconds())
                .build()

        when:
        service.save(transaction)

        then:
        1 * repositoy.compute(key, { UnaryOperator func ->
            return func.apply(null) == expectedAggregate
        })
    }

    def "should save and merge transaction"() {
        given:
        def currentMillis = Instant.now().toEpochMilli()
        def transaction = Transaction.builder()
                .amount(15.0)
                .timestamp(currentMillis)
                .build()

        def key = getIndexFromTransaction(transaction)
        def aggregate = TransactionAggregate.builder()
                .max(10.0)
                .min(5.0)
                .sum(15.0)
                .count(2L)
                .timestampInSeconds((currentMillis / 1000).toLong())
                .build()

        def expectedAggregate = TransactionAggregate.builder()
                .max(15.0)
                .min(5.0)
                .sum(30.0)
                .count(3L)
                .timestampInSeconds(aggregate.getTimestampInSeconds())
                .build()

        when:
        service.save(transaction)

        then:
        1 * repositoy.compute(key, { UnaryOperator func ->
            return func.apply(aggregate) == expectedAggregate
        })
    }

    def "should replace old transaction with new one"() {
        given:
        def currentMillis = Instant.now().toEpochMilli()
        def transaction = Transaction.builder()
                .amount(10.0)
                .timestamp(currentMillis)
                .build()

        def transactionAggregate = TransactionAggregate.builder()
                .max(10.0)
                .min(5.0)
                .sum(15.0)
                .count(2L)
                .timestampInSeconds(transaction.getTimestampInSeconds() - TIME_RANGE - 1)
                .build()

        def key = getIndexFromTransaction(transaction)

        def expectedAggregate = TransactionAggregate.builder()
                .max(transaction.getAmount())
                .min(transaction.getAmount())
                .sum(transaction.getAmount())
                .count(1L)
                .timestampInSeconds(transaction.getTimestampInSeconds())
                .build()

        when:
        service.save(transaction)

        then:
        1 * repositoy.compute(key, { UnaryOperator func ->
            return func.apply(transactionAggregate) == expectedAggregate
        })
    }

    def "should not save when timestamp is too old"() {
        given:
        def currentMillis = Instant.now().toEpochMilli() - ((TIME_RANGE + 1) * 1000)
        def transaction = Transaction.builder()
                .amount(15.0)
                .timestamp(currentMillis)
                .build()

        when:
        service.save(transaction)

        then:
        0 * repositoy.compute(_, _)
        thrown(TransactionOutOfRangeException)
    }

    def "should not save when timestamp is in the future"() {
        given:
        def currentMillis = Instant.now().toEpochMilli() + 1000

        def transaction = Transaction.builder()
                .amount(15.0)
                .timestamp(currentMillis)
                .build()

        when:
        service.save(transaction)

        then:
        0 * repositoy.compute(_, _)
        thrown(TransactionOutOfRangeException)
    }

    private Long getIndexFromTransaction(Transaction transaction) {
        return transaction.getTimestampInSeconds() % TIME_RANGE;
    }
}
