   // tailwind.config.js
   module.exports = {
     content: ['./index.html', './style.css','./src/**/*.{vue,js,ts,jsx,tsx}'],
     darkMode: 'media', // or 'class'
     theme: {
       extend: {
        maxWidth: {
               '450': '350px',
             },

         // Extend your theme here
       },
     },
     plugins: [
       require('@tailwindcss/forms'), // If you're using forms
       // Add other plugins if necessary
     ],
   };