'use client';

import { ChevronDown } from 'lucide-react';
import { useCallback, useEffect, useState } from 'react';

import { Pagination } from '@/components/general/Pagination';
import { OrdersTable } from '@/components/order/OrdersTable';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

import { OrderStatus, type TOrderDTO } from '@/DTO/order.types';
import { toastApiError } from '@/lib/utils';
import { OrderApi } from '@/utilities/orderApi';
import { OrderStatusLabels } from '@/utilities/orderUtils';

const ALL_STATUSES = Object.values(OrderStatus);

export default function OrdersPage() {
  const [orders, setOrders] = useState<TOrderDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [limit, setLimit] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [selectedStatuses, setSelectedStatuses] = useState<OrderStatus[]>(ALL_STATUSES);

  const loadOrders = useCallback(async () => {
    try {
      setLoading(true);

      const statusParam = selectedStatuses.length === 1 ? selectedStatuses[0] : undefined;

      const res = await OrderApi.fetchOrders({
        page,
        limit,
        status: statusParam,
      });

      setOrders(res.data);
      setTotalPages(res.totalPages);
    } catch (err) {
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  }, [page, limit, selectedStatuses]);

  useEffect(() => {
    void loadOrders();
  }, [loadOrders]);

  useEffect(() => {
    setPage(0);
  }, [limit, selectedStatuses]);

  const toggleStatus = (status: OrderStatus, checked: boolean) => {
    setSelectedStatuses((prev) => (checked ? [...prev, status] : prev.filter((s) => s !== status)));
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline">
              Status to show
              <ChevronDown className="ml-2 h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="start">
            {ALL_STATUSES.map((status) => (
              <DropdownMenuCheckboxItem
                key={status}
                checked={selectedStatuses.includes(status)}
                onCheckedChange={(v) => toggleStatus(status, Boolean(v))}
              >
                {OrderStatusLabels[status]}
              </DropdownMenuCheckboxItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <OrdersTable
        orders={orders}
        loading={loading}
      />

      <Pagination
        page={page}
        limit={limit}
        onLimitChange={setLimit}
        totalPages={totalPages}
        onPageChange={setPage}
      />
    </div>
  );
}
