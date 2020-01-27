function updateTickets(a) {
    let price, start, end, countInput, sumInput;
    switch (a) {
        case 0:
            price = parseFloat(document.getElementById('price-0').value);
            start = parseInt(document.getElementById('start-0').value);
            end = parseInt(document.getElementById('end-0').value);
            countInput = document.getElementById('count-0');
            sumInput = document.getElementById('sum-0');
            break;
        case 1:
            price = parseFloat(document.getElementById('price-1').value);
            start = parseInt(document.getElementById('start-1').value);
            end = parseInt(document.getElementById('end-1').value);
            countInput = document.getElementById('count-1');
            sumInput = document.getElementById('sum-1');
            break;
        case 2:
            price = parseFloat(document.getElementById('price-2').value);
            start = parseInt(document.getElementById('start-2').value);
            end = parseInt(document.getElementById('end-2').value);
            countInput = document.getElementById('count-2');
            sumInput = document.getElementById('sum-2');
            break;
        default:
            return;
    }
    const count = end - start + 1;
    const sum = price * count;
    if (count >= 0 && price >= 0 && start >= 0 && end >= 0) {
        countInput.value = count.toString();
        sumInput.value = sum.toFixed(2) + ' €';
    } else {
        countInput.value = '-';
        sumInput.value = '-';
    }
}