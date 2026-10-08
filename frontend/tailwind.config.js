/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'Manrope', 'system-ui', 'sans-serif'],
      },
      colors: {
        primary: {
          DEFAULT: '#3B6FF5',
          dark:    '#2454D8',
          light:   '#EAF0FF',
        },
        green: {
          DEFAULT: '#18B866',
          light:   '#E7F8EF',
          success: '#16A765',
        },
        navy:   '#18243A',
        dark:   '#34425A',
        body:   '#5E6B82',
        muted:  '#8994A7',
        bg:     '#F7F9FC',
        border: '#E8ECF3',
        purple: { DEFAULT: '#6C5CE7', light: '#F0EEFF' },
        orange: { DEFAULT: '#F39A27', light: '#FFF3E3' },
        blueLt: '#EEF5FF',
        error:  '#E5394F',
      },
      borderRadius: {
        card: '22px',
        btn:  '13px',
      },
      boxShadow: {
        card: '0 8px 30px rgba(30,50,90,0.06)',
        'card-hover': '0 12px 40px rgba(30,50,90,0.10)',
      },
    },
  },
  plugins: [],
}

