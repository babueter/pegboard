# PegBoard - C libraries

#### Build

```
make all
```


#### Run

```
$ ./peggame
Usage: peggame [-s | -d] [-t] [-r <peg>]

This program accepts the arguments -s, -d, -t, or -r
  -t    Displays total number of solutions in each round.
  -s    Displays every solution.  This is very verbose.
        You may want to consider ding a '-t' by itself first
  -d    Dump output without prompting.  Must be accompanied by '-s'
  -r n  Play one round, with peg 'n' empty.  Where 1 <= n <= 15.
```

