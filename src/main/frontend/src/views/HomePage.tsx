/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import { Footer } from '@/components/general/Footer';
import { Navbar } from '@/components/general/Navbar';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { CheckCircle } from 'lucide-react';

export default function HomePage() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />

      <main className="flex flex-col items-center justify-center space-y-6 p-4 text-center">
        <div>
          <h1 className="mb-2 text-2xl font-semibold">Welcome to the SWA Skeleton Project!</h1>
          <a
            className="text-blue-600 hover:underline"
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
          >
            Learn React
          </a>
        </div>

        <Alert className="w-fit">
          <div className="flex items-center space-x-2">
            <CheckCircle className="h-5 w-5 text-green-500" />
            <AlertDescription>shadcn/ui is working!</AlertDescription>
          </div>
        </Alert>
      </main>

      <Footer />
    </div>
  );
}
