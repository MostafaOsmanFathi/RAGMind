/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}", "./src/**/*.html", "./src/**/*.ts"],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        // Primary & Secondary Backgrounds
        "bg-primary": "rgb(14, 19, 27)",
        "bg-secondary": "rgb(28, 38, 54)",

        // Accent Colors
        "accent-cyan": "rgb(6, 182, 212)",
        "accent-purple": "rgb(168, 85, 247)",
        "accent-pink": "rgb(236, 72, 153)",
        "accent-blue": "rgb(59, 130, 246)",
        "accent-blue-alt": "rgb(37, 99, 235)",
        "accent-teal": "rgb(20, 184, 166)",

        // Text Colors
        "text-primary": "rgb(255, 255, 255)",
        "text-secondary": "rgb(148, 163, 184)",
        "text-muted": "rgb(100, 116, 139)",

        // Border Colors
        "border-dark": "rgb(35, 47, 67)",
      },
      borderRadius: {
        card: "12px",
        full: "9999px",
      },
      spacing: {
        "gutter": "1.5rem",
      },
      backgroundImage: {
        "gradient-hero": "linear-gradient(135deg, rgb(6, 182, 212) 0%, rgb(168, 85, 247) 50%, rgb(236, 72, 153) 100%)",
        "gradient-button": "linear-gradient(135deg, rgb(6, 182, 212) 0%, rgb(168, 85, 247) 100%)",
      },
    },
  },
  plugins: [require("@tailwindcss/typography")],
};
