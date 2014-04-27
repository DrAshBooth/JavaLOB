JavaLOB
=======

Fully functioning fast Limit Order Book written in Java

JavaLOB, is a fully functioning fast simulation of a limit order book financial exchange, developed for modelling. The aim is to allow exploration of automated trading strategies that deal with "Level 2" market data.

It is written in Java, single-threaded and opperates a standard price-time-priority. It supports both market and limit orders, as well as cancel and update functionality. The model is based on few simplifying assumptions, chief of which is zero latency: if a trader issues a new quote, that gets processed by the exchange, all other traders can react to it before any other quote is issued.

Requirements:
=============
To ensure easy distribution and use I've tried to ensure that there are no requirements other than a standard Java install. 

Check the Wiki!
===============
For details on limit order books as well as usage guides and examples, please see the wiki.

The code is open-sourced via the MIT Licence: see the LICENSE file for full text. (copied from http://opensource.org/licenses/mit-license.php)
