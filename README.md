# Evolutionary Algorithms

[![Build Status](https://travis-ci.org/Penchant/EvolutionaryAlgorithms.svg?branch=master)](https://travis-ci.org/Penchant/EvolutionaryAlgorithms)  
Using neural network, basic backprop and radial basis function.
Running without any parameters will open the GUI.  If you would rather use the command line, it is also optionally there.

For help with the command line parameters, use `java -jar Rosenbrock.jar -h`


| Flag   | Description                                                  | Default | Parameter |
|--------|--------------------------------------------------------------|:-------:|:---------:|
| -nogui | Runs the application without a GUI                           | true    | Void      |
| -h     | Displays the help text                                       |         | Void      |
| -rb    | Sets the network to use radial basis                         | false   | Void      |
| -ds    | The start point for the data (example) generation            | 0.000   | Double    |
| -de    | The end point for the data (example) generation              | 20.000  | Double    |
| -di    | The incrementation of the data point                         | 0.100   | Double    |
| -hl    | The amount of hidden layers, and the amount of nodes in each | 200,100 | String    |
| -d     | The number of dimensions the function will use               | 2       | Integer   |
| -s     | Save the weights to a given output file                      |         | String    |


Example:
```java
java -jar Rosenbrock.jar -nogui -rb -ds 0 -de 20 -di 1 -hl 200,300 -d 3
```
