package com.tnic.exmaples;
/**
 *
 */
//com.tnic.examples.increment_single = function (arg) {
function increment_single (arg) {
    return {
        func: this,
        value: arg + 1
    };
}

//print(com.tnic.examples.increment_single(1).toSource());
//var x = com.tnic.examples.increment_single(1);
//for (var i in x.func) { print ("i: "+ i); break; }
