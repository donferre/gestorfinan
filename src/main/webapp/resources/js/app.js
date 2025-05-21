var contenedor = document.querySelector('.chapter');
if (contenedor) {
/*    var btn = document.createElement("button");
    btn.innerText = "Botón desde JS externo";
    btn.onclick = function () {
        alert(1);
        zAu.send(new zk.Event(zk.Widget.$('$win_component'), "onJsClick", "EJECUTANDO DESDE JS"));
    };
    contenedor.appendChild(btn);*/

    const canvas = document.createElement("canvas");
    canvas.id = "miCanvas";
    canvas.width = 300;
    canvas.height = 150;
    canvas.style.border = "1px solid black";

    contenedor.appendChild(canvas);

    // Dibujo de ejemplo
    const ctx = canvas.getContext("2d");
    ctx.fillStyle = "#FF0000";
    ctx.fillRect(10, 10, 100, 50);

} else {
    console.warn("btnContainer no existe aún");
}




