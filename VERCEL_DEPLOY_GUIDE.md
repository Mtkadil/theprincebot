# Guida al Deploy su Vercel per Barbershop The Prince

Per pubblicare il tuo sito su Vercel usando **Next.js**, segui questi passaggi:

### 1. Crea il progetto Next.js
Apri il tuo terminale sul tuo PC e digita:
```bash
npx create-next-app@latest barbershop-web
```
(Scegli "Yes" per TypeScript, Tailwind CSS e App Router).

### 2. Copia il Codice della Pagina
Sostituisci il contenuto di `app/page.tsx` con il seguente codice professionale (già configurato per te):

```tsx
import Image from 'next/image';
import Script from 'next/script';

export default function BarbershopHome() {
  return (
    <div className="min-h-screen bg-[#FEF7FF] text-[#1D1B20] font-sans">
      {/* Header */}
      <header className="p-6 flex justify-between items-center bg-white shadow-sm">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-[#6750A4] rounded-full flex items-center justify-center text-white text-2xl">✂️</div>
          <div>
            <h1 className="text-xl font-bold tracking-tight">Barbershop The Prince</h1>
            <p className="text-xs text-[#49454F] uppercase tracking-widest">Hair Stylist & Master Fade</p>
          </div>
        </div>
        <nav className="hidden md:flex gap-6 font-medium">
          <a href="#servizi" className="hover:text-[#6750A4]">Servizi</a>
          <a href="#orari" className="hover:text-[#6750A4]">Orari</a>
          <a href="#contatti" className="hover:text-[#6750A4]">Contatti</a>
        </nav>
      </header>

      {/* Hero Section */}
      <main>
        <section className="relative h-[500px] flex items-center justify-center text-center px-4 bg-[#6750A4] text-white overflow-hidden">
          <div className="absolute inset-0 opacity-20 bg-[url('https://www.transparenttextures.com/patterns/carbon-fibre.png')]"></div>
          <div className="relative z-10 max-w-2xl">
            <h2 className="text-5xl font-extrabold mb-4">Precisione in ogni sfumatura.</h2>
            <p className="text-lg mb-8 opacity-90">Esperienza in Italia e all'estero. Il migliore in tutti gli stili, con cura maniacale per i dettagli.</p>
            <a href="https://wa.me/393756866511" className="bg-[#25D366] hover:bg-[#128C7E] text-white px-8 py-4 rounded-full font-bold text-lg transition-transform hover:scale-105 shadow-lg">
              Prenota il tuo Taglio
            </a>
          </div>
        </section>

        {/* Info & Orari */}
        <section id="orari" className="py-16 px-6 max-w-4xl mx-auto grid md:grid-cols-2 gap-12">
          <div className="bg-white p-8 rounded-[32px] shadow-sm border border-[#CAC4D0]">
            <h3 className="text-2xl font-bold mb-6 flex items-center gap-2">
              <span className="text-[#6750A4]">🕒</span> Orari di Apertura
            </h3>
            <ul className="space-y-3">
              <li className="flex justify-between border-b pb-2"><span>Lunedì</span> <span className="font-bold text-[#6750A4]">12:00 - 19:00</span></li>
              <li className="flex justify-between border-b pb-2"><span>Martedì - Sabato</span> <span className="font-bold text-[#6750A4]">10:00 - 20:00</span></li>
              <li className="flex justify-between"><span>Domenica</span> <span className="font-bold text-[#6750A4]">10:00 - 16:00</span></li>
            </ul>
          </div>

          <div className="bg-[#EADDFF] p-8 rounded-[32px] shadow-sm">
            <h3 className="text-2xl font-bold mb-4 text-[#21005D]">Contatti & Sede</h3>
            <p className="mb-4 text-[#21005D] opacity-80 italic">"Precisione nelle sfumature e cura dei dettagli. Uno stile unico per ogni cliente."</p>
            <div className="space-y-4">
              <div className="flex items-center gap-3">
                <span className="text-xl">📍</span>
                <span>Via della Sfumatua 42, Roma</span>
              </div>
              <div className="flex items-center gap-3">
                <span className="text-xl">📞</span>
                <span className="font-bold">+39 375 6866511</span>
              </div>
            </div>
          </div>
        </section>
      </main>

      {/* Widget Bot AI (Prince Bot) */}
      <Script id="bot-config" strategy="afterInteractive">
        {`
          window.CustomerBotConfig = {
            botName: "Prince Bot",
            businessName: "Barbershop The Prince",
            whatsappNumber: "+393756866511",
            themeColor: "#6750A4"
          };
        `}
      </Script>
      <Script 
        src="https://ais-dev-ue4wvu4diodgtuu2zrq3ln-401614764740.europe-west2.run.app/widget.js" 
        strategy="afterInteractive" 
      />
    </div>
  );
}
```

### 3. Deploy su Vercel
1. Carica il tuo codice su un repository **GitHub**.
2. Vai su [Vercel.com](https://vercel.com) e connetti il tuo account GitHub.
3. Importa il progetto `barbershop-web` e clicca su **Deploy**.

Il tuo sito sarà online con il chatbot attivo!
