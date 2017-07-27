// Find all the pre elements and do syntax highlighting on them using hljs.

addEventListener('load', start);

function start() {
    var pres = document.querySelectorAll("pre");
    for (var i in pres) hljs.highlightBlock(pres[i]);
}
