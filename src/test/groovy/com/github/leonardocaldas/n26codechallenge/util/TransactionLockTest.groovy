package com.github.leonardocaldas.n26codechallenge.util

import spock.lang.Specification

class TransactionLockTest extends Specification {

    def "should get lock for key"() {
        given:
        def key = 15L
        def anotherKey = 20L

        when:
        def lock = TransactionLock.getLockForKey(key)
        def sameLock = TransactionLock.getLockForKey(key)
        def anotherLock = TransactionLock.getLockForKey(anotherKey)

        then:
        lock != null
        anotherLock != null
        lock instanceof Object
        anotherLock instanceof Object
        lock == sameLock
        anotherLock != lock
    }

}