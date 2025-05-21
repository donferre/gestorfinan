zk.afterMount(function() {
    console.log("Buscando elementos...");
    console.log(document.querySelector(".header--nav-toggle"));
    console.log(document.querySelector(".sidebar"));
    console.log(document.querySelector(".sidebar-link"));

    const toggler = document.querySelector(".header--nav-toggle");
    if (toggler) {
        toggler.addEventListener("click", function() {
            const sidebar = document.querySelector(".sidebar");
            if (sidebar) {
                sidebar.classList.toggle("collapsed");
            } else {
                console.error("No se encontró la sidebar");
            }
        });
    } else {
        console.error("No se encontró .header--nav-toggle");
    }

    document.querySelectorAll(".sidebar-link").forEach(link => {
        link.addEventListener("click", function(event) {
            //event.preventDefault(); // Evita la navegación por hash

            // Encuentra el div siguiente que contiene el submenú
            const submenu = this.parentElement.querySelector(".sidebar-dropdown");

            if (submenu) {
                submenu.classList.toggle("collapse"); // Alterna la clase
            }
        });
    });

});
