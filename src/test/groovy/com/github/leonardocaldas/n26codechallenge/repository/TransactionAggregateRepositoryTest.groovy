package com.github.leonardocaldas.n26codechallenge.repository

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate
import com.github.leonardocaldas.n26codechallenge.repository.impl.TransactionAggregateRepositoryImpl
import spock.lang.Specification

import static java.util.Arrays.asList
import static java.util.Collections.emptyList

class TransactionAggregateRepositoryTest extends Specification {

    def TIME_RANGE = 60
    def repositoy = new TransactionAggregateRepositoryImpl(TIME_RANGE)

    def "should not find transactions"() {
        when:
        def all = repositoy.findAll()

        then:
        all == emptyList()
    }

    def "should delete all transactions"() {
        given:
        def id = 1

        def transactionAggregate = TransactionAggregate.builder()
                .count(2L)
                .max(20.0)
                .min(15.0)
                .sum(35.0)
                .timestampInSeconds(10)
                .build()

        when:
        repositoy.compute(id, { transactionAggregate })
        def all = repositoy.findAll()

        repositoy.deleteAll()
        def allAfterDelete = repositoy.findAll()

        then:
        all == asList(transactionAggregate)
        allAfterDelete == emptyList()
    }
}
