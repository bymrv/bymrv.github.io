document.addEventListener('DOMContentLoaded', function() {
    AOS.init({ duration: 800, once: false });

    var header = document.getElementById('main-header');
    window.addEventListener('scroll', function() {
        if (window.scrollY > 50) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });

    var links = document.querySelectorAll('nav a');
    for (var i = 0; i < links.length; i++) {
        links[i].addEventListener('click', function(e) {
            e.preventDefault();
            var targetId = this.getAttribute('href');
            var targetElement = document.querySelector(targetId);
            if (targetElement) {
                targetElement.scrollIntoView({ behavior: 'smooth' });
            }
        });
    }

    var slides = document.querySelectorAll('.carousel-slide');
    var dots = document.querySelectorAll('.dot');
    var currentSlide = 0;
    var slideInterval;
    var carouselContainer = document.querySelector('.carousel-container');
    var isPaused = false;

    function showSlide(index) {
        if (index >= slides.length) index = 0;
        if (index < 0) index = slides.length - 1;
        
        for (var i = 0; i < slides.length; i++) {
            slides[i].classList.remove('active');
            dots[i].classList.remove('active');
        }
        
        slides[index].classList.add('active');
        dots[index].classList.add('active');
        currentSlide = index;
    }

    function nextSlide() {
        if (!isPaused) {
            showSlide(currentSlide + 1);
        }
    }

    function startCarousel() {
        slideInterval = setInterval(nextSlide, 4000);
    }

    function stopCarousel() {
        clearInterval(slideInterval);
    }

    showSlide(0);
    startCarousel();

    if (carouselContainer) {
        carouselContainer.addEventListener('mouseenter', function() {
            isPaused = true;
        });
        carouselContainer.addEventListener('mouseleave', function() {
            isPaused = false;
        });
    }

    for (var i = 0; i < dots.length; i++) {
        (function(index) {
            dots[index].addEventListener('click', function() {
                showSlide(index);
                stopCarousel();
                startCarousel();
            });
        })(i);
    }

    var tabBtns = document.querySelectorAll('.tab-btn');
    var tabPanes = document.querySelectorAll('.tab-pane');

    for (var i = 0; i < tabBtns.length; i++) {
        tabBtns[i].addEventListener('click', function() {
            var tabId = this.getAttribute('data-tab');
            
            for (var j = 0; j < tabBtns.length; j++) {
                tabBtns[j].classList.remove('active');
            }
            for (var k = 0; k < tabPanes.length; k++) {
                tabPanes[k].classList.remove('active');
            }
            
            this.classList.add('active');
            document.getElementById(tabId).classList.add('active');
        });
    }

    var acordeonHeaders = document.querySelectorAll('.acordeon-header');
    for (var i = 0; i < acordeonHeaders.length; i++) {
        acordeonHeaders[i].addEventListener('click', function() {
            var item = this.parentElement;
            var isActive = item.classList.contains('active');
            
            var allItems = document.querySelectorAll('.acordeon-item');
            for (var j = 0; j < allItems.length; j++) {
                allItems[j].classList.remove('active');
            }
            
            if (!isActive) {
                item.classList.add('active');
            }
        });
    }

    var lbModal = document.getElementById('lightbox-modal');
    var lbImg = document.getElementById('lightbox-img');
    var triggers = document.querySelectorAll('.lightbox-trigger');
    for (var i = 0; i < triggers.length; i++) {
        triggers[i].addEventListener('click', function() {
            lbModal.style.display = 'block';
            lbImg.src = this.src;
        });
    }
    document.querySelector('.close-lightbox').addEventListener('click', function() {
        lbModal.style.display = 'none';
    });
    lbModal.addEventListener('click', function(e) {
        if (e.target === lbModal) {
            lbModal.style.display = 'none';
        }
    });

    var form = document.getElementById('form-reserva');
    var modal = document.getElementById('modal-confirmacion');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        var nombre = document.getElementById('nombre');
        var personas = document.getElementById('personas');
        var fecha = document.getElementById('fecha');
        var email = document.getElementById('email');
        
        var errorSpans = document.querySelectorAll('.error-message');
        for (var i = 0; i < errorSpans.length; i++) {
            errorSpans[i].textContent = '';
        }
        
        var inputs = document.querySelectorAll('input');
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].classList.remove('error');
        }
        
        var valid = true;
        
        if (nombre.value.trim().length < 3) {
            showError(nombre, 'El nombre debe tener al menos 3 caracteres');
            valid = false;
        }
        
        var personasNum = parseInt(personas.value);
        if (isNaN(personasNum) || personasNum < 1 || personasNum > 5) {
            showError(personas, 'El número de personas debe ser entre 1 y 5');
            valid = false;
        }
        
        if (fecha.value === '') {
            showError(fecha, 'Selecciona una fecha');
            valid = false;
        } else {
            var fechaSeleccionada = new Date(fecha.value);
            var hoy = new Date();
            hoy.setHours(0, 0, 0, 0);
            if (fechaSeleccionada < hoy) {
                showError(fecha, 'La fecha no puede ser anterior a hoy');
                valid = false;
            }
        }
        
        if (email.value.trim() === '') {
            showError(email, 'El correo no puede estar vacío');
            valid = false;
        } else if (email.value.indexOf('@') === -1 || email.value.indexOf('.') === -1) {
            showError(email, 'Introduce un correo electrónico válido');
            valid = false;
        }
        
        if (valid) {
            modal.style.display = 'flex';
            form.reset();
        }
    });
    
    function showError(input, message) {
        input.classList.add('error');
        var parent = input.parentElement;
        var errorSpan = parent.querySelector('.error-message');
        if (errorSpan) {
            errorSpan.textContent = message;
        }
    }
    
    document.querySelector('.close-modal').addEventListener('click', function() {
        modal.style.display = 'none';
    });
    document.querySelector('.btn-modal-ok').addEventListener('click', function() {
        modal.style.display = 'none';
    });
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });
});