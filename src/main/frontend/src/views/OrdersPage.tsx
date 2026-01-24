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

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
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

  const [selectedStatus, setSelectedStatus] = useState<OrderStatus | null>(null);
  const [sort, setSort] = useState<'timestamp,asc' | 'timestamp,desc'>('timestamp,desc');

  const loadOrders = useCallback(async () => {
    try {
      setLoading(true);

      const statusParam = selectedStatus ? [selectedStatus] : undefined;

      const res = await OrderApi.fetchOrders({
        page,
        limit,
        status: statusParam,
        sort,
      });

      setOrders(res.data);
      setTotalPages(res.totalPages);
    } catch (err) {
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  }, [page, limit, selectedStatus, sort]);

  useEffect(() => {
    void loadOrders();
  }, [loadOrders]);

  useEffect(() => {
    setPage(0);
  }, [limit, selectedStatus, sort]);

  const selectStatus = (status: OrderStatus) => {
    setSelectedStatus((prev) => (prev === status ? null : status));
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline">
              {selectedStatus ? OrderStatusLabels[selectedStatus] : 'All statuses'}
              <ChevronDown className="ml-2 h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="start">
            {ALL_STATUSES.map((status) => (
              <DropdownMenuCheckboxItem
                key={status}
                checked={selectedStatus === status}
                onCheckedChange={() => selectStatus(status)}
              >
                {OrderStatusLabels[status]}
              </DropdownMenuCheckboxItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>

        <Select
          value={sort}
          onValueChange={(v) => setSort(v as typeof sort)}
        >
          <SelectTrigger className="w-[220px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="timestamp,desc">Newest first</SelectItem>
            <SelectItem value="timestamp,asc">Oldest first</SelectItem>
          </SelectContent>
        </Select>
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
