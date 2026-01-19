'use client';

import { useCallback, useEffect, useState } from 'react';

import { Pagination } from '@/components/general/Pagination';

import { SubscriptionFilters } from '@/components/subscription/SubscriptionsFilters';
import { SubscriptionTable } from '@/components/subscription/SubscriptionsTable';
import type { NotificationType } from '@/DTO/notification.types';
import type { SubscriptionType, TPopulatedSubscriptionDTO } from '@/DTO/subscription.types';
import { toastApiError } from '@/lib/utils';
import { SubscriptionApi } from '@/utilities/subscriptionApi';

export default function SubscriptionsPage() {
  const [subscriptions, setSubscriptions] = useState<TPopulatedSubscriptionDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [limit, setLimit] = useState(12);
  const [totalPages, setTotalPages] = useState(0);

  const [draftTypes, setDraftTypes] = useState<SubscriptionType[]>([]);
  const [draftChannels, setDraftChannels] = useState<NotificationType[]>([]);
  const [draftSort, setDraftSort] = useState('id,asc');

  const [appliedTypes, setAppliedTypes] = useState<SubscriptionType[]>([]);
  const [appliedChannels, setAppliedChannels] = useState<NotificationType[]>([]);
  const [appliedSort, setAppliedSort] = useState('id,asc');

  const loadSubscriptions = useCallback(async () => {
    try {
      setLoading(true);

      const response = await SubscriptionApi.getUserSubscriptionsPagePopulated({
        page,
        limit,
        sort: appliedSort,
        types: appliedTypes.length > 0 ? appliedTypes : undefined,
        channels: appliedChannels.length > 0 ? appliedChannels : undefined,
      });

      setSubscriptions(response.data);
      setTotalPages(response.totalPages);
    } catch (err) {
      console.error(err);
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  }, [page, limit, appliedSort, appliedTypes, appliedChannels]);

  useEffect(() => {
    void loadSubscriptions();
  }, [loadSubscriptions]);

  const handleApplyFilters = () => {
    setPage(0);
    setAppliedTypes(draftTypes);
    setAppliedChannels(draftChannels);
    setAppliedSort(draftSort);
  };

  useEffect(() => {
    setPage(0);
  }, [limit]);

  return (
    <div className="flex flex-col gap-8 lg:flex-row">
      <SubscriptionFilters
        types={draftTypes}
        setTypes={setDraftTypes}
        channels={draftChannels}
        setChannels={setDraftChannels}
        sort={draftSort}
        setSort={setDraftSort}
        onApplyFilters={handleApplyFilters}
      />

      <div className="flex-1">
        <SubscriptionTable
          subscriptions={subscriptions}
          loading={loading}
          onChanged={() => void loadSubscriptions()}
        />

        <Pagination
          page={page}
          limit={limit}
          onLimitChange={setLimit}
          totalPages={totalPages}
          onPageChange={setPage}
        />
      </div>
    </div>
  );
}
