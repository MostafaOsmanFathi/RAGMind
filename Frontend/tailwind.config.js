/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}", "./src/**/*.html", "./src/**/*.ts"],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        /* Backgrounds */
        "bg-main": "rgb(var(--bg-main) / <alpha-value>)",
        "bg-card": "rgb(var(--bg-card) / <alpha-value>)",
        "bg-muted": "rgb(var(--bg-muted) / <alpha-value>)",
        /* Text */
        "text-primary": "rgb(var(--text-primary) / <alpha-value>)",
        "text-secondary": "rgb(var(--text-secondary) / <alpha-value>)",
        "text-muted": "rgb(var(--text-muted) / <alpha-value>)",
        /* Border */
        "border-default": "rgb(var(--border) / <alpha-value>)",
        /* Primary (Sage) */
        "primary-default": "rgb(var(--primary) / <alpha-value>)",
        "primary-hover": "rgb(var(--primary-hover) / <alpha-value>)",
        /* Secondary (Futuristic Blue) */
        "secondary-default": "rgb(var(--secondary) / <alpha-value>)",
        "secondary-hover": "rgb(var(--secondary-hover) / <alpha-value>)",
        /* Accent (Sand Gold) */
        "accent": "rgb(var(--accent) / <alpha-value>)",
        "accent-soft": "rgb(var(--accent-soft) / <alpha-value>)",
        /* States */
        "success": "rgb(var(--success) / <alpha-value>)",
        "warning": "rgb(var(--warning) / <alpha-value>)",
        "error": "rgb(var(--error) / <alpha-value>)",
      },
      borderRadius: {
        card: "12px",
        full: "9999px",
      },
      spacing: {
        gutter: "1.5rem",
      },
      backgroundImage: {
        "gradient-hero":
          "linear-gradient(135deg, rgb(var(--primary)) 0%, rgb(var(--secondary)) 100%)",
        "gradient-button":
          "linear-gradient(135deg, rgb(var(--primary)) 0%, rgb(var(--primary-hover)) 100%)",
      },
      animation: {
        "fade-in-up": "fade-in-up 0.6s ease-out forwards",
        "scale-in": "scale-in 0.4s ease-out forwards",
      },
      transitionDuration: {
        400: "400ms",
      },
    },
  },
  plugins: [require("@tailwindcss/typography")],
};
