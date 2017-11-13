# Evolutionary Algorithms

[![Build Status](https://travis-ci.org/Penchant/EvolutionaryAlgorithms.svg?branch=master)](https://travis-ci.org/Penchant/EvolutionaryAlgorithms)  
Using evolutionary algorithms
Running without any parameters will open the GUI.  If you would rather use the command line, it is also optionally there.

For help with the command line parameters, use `java -jar evolution.jar -h`


| Flag   | Description                                                  | Default   | Parameter |
|--------|--------------------------------------------------------------|:---------:|:---------:|
| -nogui | Runs the application without a GUI                           | true      | Void      |
| -h     | Displays the help text                                       |           | Void      |
| -hl    | The amount of hidden layers, and the amount of nodes in each | 40,40     | String    |
| -s     | Save the weights to a given output file                      |           | String    |
| -p     | Population size                                              | 10        | Integer   |
| -o     | Offspring count                                              | 10        | Integer   |
| -lr    | Learning Rate                                                | 0.010     | Double    |
| -a     | Algorithm (bp, ga, ds, de)                                   | ga        | String    |
| -f     | Data File                                                    | iris.data | String    |
| -m     | Mutation Rate                                                | 0.050     | Double    |
| -b     | Beta Rate                                                    | 0.100     | Double    |


Example:
```java
java -jar evolution.jar -nogui
```
