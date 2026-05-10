lp:
    #!/usr/bin/env bash
    set -e
    rm -rf src/main/gen
    ./gradlew generateLexer generateParser

c:
    ./gradlew check
