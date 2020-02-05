# vault

Application vault generated from https://github.com/mikeananev/sapp.git template.

[![CircleCI](https://circleci.com/gh/redstarssystems/vault/tree/develop.svg?style=svg)](https://circleci.com/gh/redstarssystems/vault/tree/develop)
[![TravisCI](https://www.travis-ci.org/redstarssystems/vault.svg?branch=develop)](https://www.travis-ci.org/redstarssystems/vault)

## Usage

### Compile Java classes

Uncomment  :java-source-paths and :javac-options in deps.edn.

```bash
$ clojure -R:bg -A:javac
```

### REPL

```bash
$ clojure -A:repl
nREPL server started on port 56785 on host localhost - nrepl://localhost:56785
```
or, if you want to compile sources from repl, then include :uberjar alias

```bash
$ clojure -R:bg -A:repl
nREPL server started on port 56788 on host localhost - nrepl://localhost:56788
```

### Tests

```bash
$ clojure -A:test
```

### Uberjar

```bash
$ clojure -R:bg -A:uberjar
```

### Standalone app

To build standalone app run the following command on Java 9+ :

```bash
$ clojure -R:bg -A:standalone
```

This will create a bundle (app + custom JDK) and its archive,
which can be run by : `$ bin/run.sh` from the root folder of bundle.

## License

Copyright © 2020 Mike Ananev
Red Stars Systems

Distributed under the Apache License 2.0  
http://www.apache.org/licenses/LICENSE-2.0.txt

