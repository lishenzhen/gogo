let lints = null;
let input = null;
let lastInputValue = null;
let xhttp = null;
function requestLints() {
    // check
    if (lints == null) {
        lints = document.getElementById("lints");
    }
    if (input == null) {
        input = document.getElementById("input");
    }
    // read input value
    let value = input.value.trim();
    if (lastInputValue != value) {
        lastInputValue = value;
    } else {
        return;
    }
    if (value == "") {
        return;
    }
    // clear all children in datalist
    while(lints.childElementCount > 0) {
        lints.children[0].remove();
    }
    // http request
    if (xhttp != null) {
        xhttp.abort();
    }
    xhttp = new XMLHttpRequest();
    xhttp.timeout = 2000;
    xhttp.onreadystatechange = function () {
        if (this.readyState != 4 || this.status != 200) {
            return;
        }
        let responseText = this.responseText;
        let data = JSON.parse(responseText);
        data.lints.forEach(l => lints.appendChild(new Option(l.replace("<b>", "").replace("</b>", ""))));
        xhttp = null;
    };
    xhttp.open('GET', "/api/lint?q=" + value);
    xhttp.send();
};