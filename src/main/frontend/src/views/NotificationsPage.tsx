'use client';

import { useCallback, useEffect, useState } from 'react';

import { Pagination } from '@/components/general/Pagination';
import { NotificationFilters } from '@/components/notification/NotificationFilters';
import { NotificationTable } from '@/components/notification/NotificationTable';

import type { TNotificationResponseDTO } from '@/DTO/notification.types';
import { NotificationStatus, NotificationType } from '@/DTO/notification.types';
import { toastApiError } from '@/lib/utils';
import { NotificationApi } from '@/utilities/notificationApi';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState<TNotificationResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [limit, setLimit] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [draftStatus, setDraftStatus] = useState<NotificationStatus | null>(null);
  const [draftChannel, setDraftChannel] = useState<NotificationType | null>(null);
  const [draftSort, setDraftSort] = useState('timestamp,desc');

  const [appliedStatus, setAppliedStatus] = useState<NotificationStatus | undefined>();
  const [appliedChannel, setAppliedChannel] = useState<NotificationType | undefined>();
  const [appliedSort, setAppliedSort] = useState('timestamp,desc');

  const loadNotifications = useCallback(async () => {
    try {
      setLoading(true);

      const response = await NotificationApi.getUserNotifications({
        page,
        limit,
        sort: appliedSort,
        status: appliedStatus,
        channel: appliedChannel,
      });

      setNotifications(response.data);
      setTotalPages(response.totalPages);
    } catch (err) {
      console.error(err);
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  }, [page, limit, appliedStatus, appliedChannel, appliedSort]);

  useEffect(() => {
    void loadNotifications();
  }, [loadNotifications]);

  const handleApplyFilters = () => {
    setPage(0);
    setAppliedStatus(draftStatus ?? undefined);
    setAppliedChannel(draftChannel ?? undefined);
    setAppliedSort(draftSort);
  };

  useEffect(() => {
    setPage(0);
  }, [limit]);

  return (
    <div className="flex flex-col gap-8 lg:flex-row">
      <NotificationFilters
        status={draftStatus}
        setStatus={setDraftStatus}
        channel={draftChannel}
        setChannel={setDraftChannel}
        sort={draftSort}
        setSort={setDraftSort}
        onApplyFilters={handleApplyFilters}
      />

      <div className="flex-1">
        <NotificationTable
          notifications={notifications}
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
    </div>
  );
}
