import { CheckCircle } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ROUTES } from '@/utilities/routes.paths';

export default function PaymentSuccessPage() {
  const { orderNum } = useParams<{ orderNum: string }>();
  const navigate = useNavigate();

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <Card className="w-full max-w-md text-center">
        <CardHeader className="space-y-3">
          <div className="flex justify-center">
            <CheckCircle className="h-12 w-12 text-green-600" />
          </div>
          <CardTitle className="text-2xl">Payment successful!</CardTitle>
        </CardHeader>

        <CardContent className="space-y-4">
          <p className="text-muted-foreground">Thank you for your purchase.</p>
          <p className="text-sm font-medium text-green-600">
            An email confirmation has been sent to your registered email address.
          </p>

          <p className="text-sm">
            Your order
            {orderNum && (
              <>
                {' '}
                <span className="font-medium">#{orderNum}</span>
              </>
            )}{' '}
            has been confirmed and is now being prepared for shipment.
          </p>

          <p className="text-sm text-muted-foreground">You’ll receive a notification once your items are on the way.</p>

          <Button
            className="mt-4 w-full"
            onClick={() => navigate(ROUTES.HOME, { replace: true })}
          >
            Back to home
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
