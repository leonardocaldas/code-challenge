package com.github.leonardocaldas.n26codechallenge.repository

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate
import com.github.leonardocaldas.n26codechallenge.repository.impl.TransactionAggregateRepositoryImpl
import spock.lang.Specification

import static java.util.Arrays.asList
import static java.util.Collections.emptyList

class TransactionAggregateRepositoryTest extends Specification {

    def TIME_RANGE = 60
    def repositoy = new TransactionAggregateRepositoryImpl(TIME_RANGE)

    def "should save transaction aggregates"() {
        given:
        def transactionAggregate = TransactionAggregate.builder()
                .count(2L)
                .max(20.0)
                .min(15.0)
                .sum(35.0)
                .timestampInSeconds(10)
                .build()

        def transactionAggregate2 = TransactionAggregate.builder()
                .count(3L)
                .max(10.0)
                .min(5.0)
                .sum(15.0)
                .timestampInSeconds(20)
                .build()

        when:
        repositoy.save(1, transactionAggregate)
        repositoy.save(2, transactionAggregate2)

        def found1 = repositoy.find(1)
        def found2 = repositoy.find(2)

        def all = repositoy.findAll()

        then:
        found1 == Optional.ofNullable(transactionAggregate)
        found2 == Optional.ofNullable(transactionAggregate2)
        all == asList(transactionAggregate, transactionAggregate2)
    }

    def "should not find transactions"() {
        given:
        def id = 1

        when:
        def notFound = repositoy.find(id)
        def all = repositoy.findAll()

        then:
        notFound == Optional.ofNullable(null)
        all == emptyList()
    }

    def "should update transaction"() {
        given:
        def id = 1

        def transactionAggregate = TransactionAggregate.builder()
                .count(2L)
                .max(20.0)
                .min(15.0)
                .sum(35.0)
                .timestampInSeconds(10)
                .build()

        def updateAggregate = TransactionAggregate.builder()
                .count(3L)
                .max(20.0)
                .min(15.0)
                .sum(45.0)
                .timestampInSeconds(20)
                .build()

        when:
        repositoy.save(id, transactionAggregate)
        repositoy.save(id, updateAggregate)

        def found = repositoy.find(id)
        def all = repositoy.findAll()

        then:
        found == Optional.ofNullable(updateAggregate)
        all == asList(updateAggregate)
    }
}
