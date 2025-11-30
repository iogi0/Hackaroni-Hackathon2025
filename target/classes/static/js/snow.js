const canvas = document.getElementById("snow");
const ctx = canvas.getContext("2d");

let width, height;

function resizeCanvas() {
    width = canvas.width = window.innerWidth;
    height = canvas.height = window.innerHeight;
}
window.addEventListener("resize", resizeCanvas);
resizeCanvas();

const maxFlakes = 350, flakes = [];

for (let i = 0; i < maxFlakes; i++) {
    flakes.push({
        x: Math.random() * width,
        y: Math.random() * height,
        r: Math.random() * 2 + 1,
        d: Math.random() * 0.6 + 0.3,
    });
}

let angle = 0;

function drawSnow() {
    ctx.clearRect(0, 0, width, height);

    ctx.fillStyle = "white";
    ctx.beginPath();
    for (let i = 0; i < maxFlakes; i++) {
        const f = flakes[i];
        ctx.moveTo(f.x, f.y);
        ctx.arc(f.x, f.y, f.r, 0, Math.PI * 2);
    }
    ctx.fill();

    updateSnow();
    requestAnimationFrame(drawSnow);
}

function updateSnow() {
    angle += 0.005;

    for (let i = 0; i < maxFlakes; i++) {
        const f = flakes[i];

        f.y += Math.pow(f.d, 1.8) + 0.01;
        f.x += Math.sin(angle) * f.d * 0.8;

        if (f.y > height) flakes[i] = {x: Math.random() * width, y: -10, r: f.r, d: f.d};


        if (f.x > width + 10) f.x = -10;
        if (f.x < -10) f.x = width + 10;
    }
}

drawSnow();
