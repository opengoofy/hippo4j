module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {},
  },
  plugins: [],
  corePlugins: { preflight: false },
  // set dark mode. example <h1 className="text-black dark:text-white">Hello, World!</h1>
  darkMode: ["class", '[data-theme="dark"]'],
};
