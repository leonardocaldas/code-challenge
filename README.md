# N26 Code Challenge

[![Build Status](https://travis-ci.org/leonardocaldas/n26-code-challenge.svg?branch=master)](https://travis-ci.org/leonardocaldas/n26-code-challenge)
[![codecov](https://codecov.io/gh/leonardocaldas/n26-code-challenge/branch/master/graph/badge.svg)](https://codecov.io/gh/leonardocaldas/n26-code-challenge)

## Introduction
The purpose of this project is to collect statistics of all transactions that happened in the last minute.  

## Prerequisites
- Java 8

## Build

To build this project run the command below:

```bash
./mvnw clean install
```
or
```bash
mvn clean install
```

## How to run

Start the application by running the command below in the root of the project after the previous step has finished:

```bash
java -jar ./target/n26-code-challenge.jar
```


## Endpoints

#### Transactions

`POST /transactions`

| Name      | Type   | Description                                          |
|-----------|--------|------------------------------------------------------|
| amount    | Double | The transaction amount                               |
| timestamp | Long   | Transaction time in epoch in millis in UTC time zone |

##### Example

```bash
$ curl -X POST -i 'http://localhost:8080/transactions'  \
    -H 'Content-Type: application/json' \
    -H 'Accept: application/json' \
    -d '{
        "amount": 252.5,
        "timestamp": 1532317213000
    }'
```

##### Response

The transaction request will return:
    
- **201 - Created**: When transaction is successfully created.
- **204 - No Content**: When transaction is reject by being too old.

Both of them with **empty body**. Like in the example below:

```bash
HTTP/1.1 201 
```

#### Statistics

The purpose of this endpoint this to get statistics of all transactions that happened in the last minute. 

`GET /statistics`

##### Example
```bash
$ curl -X -i GET 'http://localhost:8080/statistics' -H 'Accept: application/json'
```

##### Response

```bash
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Mon, 23 Jul 2018 03:55:27 GMT

{
    "max": 250,
    "min": 5,
    "avg": 102.12,
    "sum": 408.5,
    "count": 4
}
```

## Requirements and Solutions

**1. Endpoints have to execute in constant time and memory (O(1)):**<br>
To meet this requirement, a key is created by running a modulo of 60 against the transaction timestamp. By doing this, we can aggregate all the transactions with the same key inside a HashMap.      

**2. The API have to be threadsafe with concurrent requests:**<br>
To meet this requirement two things were used:
- A **ConcurrentHashMap** with only 60 positions that holds all the transaction statistics.
- A synchronized block by the transaction key. This is used to guarantee that transactions with the same key do not run at the same time.

## Additional Information

- Unit tests were written using Groovy and Spock Framework for readability and productivity purposes.
- The application supports internationalization. Currently only 2 languages are configured to be supported: English(en, default) and Brazilian Portuguese(pt-BR). The change of languages can be achieved by using the header "Accept-Language".
- A global exception handler is configured to normalize all the error responses.