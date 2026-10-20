/**
 * ShopSphere E-Commerce Client Scripts
 */

document.addEventListener("DOMContentLoaded", function () {

    // 1. Quantity Selector Controls
    document.querySelectorAll(".qty-btn").forEach(function (button) {
        button.addEventListener("click", function () {
            const isPlus = this.classList.contains("plus");
            const input = this.parentElement.querySelector(".qty-input");
            if (!input) return;

            let currentVal = parseInt(input.value) || 1;
            const min = parseInt(input.getAttribute("min")) || 1;
            const max = parseInt(input.getAttribute("max")) || 999;

            if (isPlus) {
                if (currentVal < max) {
                    input.value = currentVal + 1;
                } else {
                    alert("Maximum stock limit of " + max + " reached.");
                }
            } else {
                if (currentVal > min) {
                    input.value = currentVal - 1;
                }
            }

            // Auto-submit if inside a cart update form
            const form = input.closest(".cart-qty-form");
            if (form) {
                form.submit();
            }
        });
    });

    // 2. AJAX Add-to-Cart Buttons
    document.querySelectorAll(".ajax-add-cart").forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const form = this.closest("form");
            if (!form) return;

            const originalText = this.innerHTML;
            this.innerHTML = "Adding...";
            this.disabled = true;

            const formData = new FormData(form);
            const params = new URLSearchParams(formData);

            fetch(form.action, {
                method: "POST",
                body: params,
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    "Content-Type": "application/x-www-form-urlencoded"
                }
            })
            .then(response => response.json())
            .then(data => {
                btn.innerHTML = "✓ Added!";
                btn.classList.add("btn-success");
                btn.classList.remove("btn-primary");

                // Update cart badges in navbar
                if (data.cartCount !== undefined) {
                    document.querySelectorAll(".cart-count-badge").forEach(badge => {
                        badge.textContent = data.cartCount;
                    });
                }

                setTimeout(() => {
                    btn.innerHTML = originalText;
                    btn.classList.remove("btn-success");
                    btn.classList.add("btn-primary");
                    btn.disabled = false;
                }, 1500);
            })
            .catch(() => {
                // Fallback to normal form submit on any error
                form.submit();
            });
        });
    });

    // 3. AJAX Wishlist Buttons
    document.querySelectorAll(".ajax-wishlist-btn").forEach(function (btn) {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const form = this.closest("form");
            if (!form) return;

            const formData = new FormData(form);
            const params = new URLSearchParams(formData);

            fetch(form.action, {
                method: "POST",
                body: params,
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    "Content-Type": "application/x-www-form-urlencoded"
                }
            })
            .then(response => {
                if (response.status === 401) {
                    window.location.href = (window.ShopSphereContext || "") + "/login?error=auth_required";
                    return;
                }
                return response.json();
            })
            .then(data => {
                if (!data) return;
                if (data.status === "success") {
                    btn.classList.toggle("active");
                    btn.style.color = btn.classList.contains("active") ? "#ef4444" : "#64748b";
                    if (data.count !== undefined) {
                        document.querySelectorAll(".wishlist-count-badge").forEach(badge => {
                            badge.textContent = data.count;
                        });
                    }
                }
            })
            .catch(() => {
                form.submit();
            });
        });
    });

    // 4. Alert Auto-Dismiss
    document.querySelectorAll(".alert-close").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const alert = this.closest(".alert");
            if (alert) alert.remove();
        });
    });

    // 5. Destructive Action Confirmation
    document.querySelectorAll(".confirm-action").forEach(function (element) {
        element.addEventListener("click", function (e) {
            const message = this.getAttribute("data-confirm") || "Are you sure you want to proceed?";
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });

    // 6. Checkout Address Option Toggle
    const addressRadios = document.querySelectorAll("input[name='addressOption']");
    const newAddressSection = document.getElementById("newAddressFields");
    if (addressRadios.length && newAddressSection) {
        addressRadios.forEach(function (radio) {
            radio.addEventListener("change", function () {
                if (this.value === "new") {
                    newAddressSection.style.display = "block";
                } else {
                    newAddressSection.style.display = "none";
                }
            });
        });
    }

    // 7. Image Error Fallback Handler
    document.querySelectorAll("img").forEach(function (img) {
        img.addEventListener("error", function () {
            if (!this.getAttribute("data-fallback-tried")) {
                this.setAttribute("data-fallback-tried", "true");
                const context = window.ShopSphereContext || "";
                this.src = context + "/images/products/placeholder.svg";
            }
        });
    });
});
