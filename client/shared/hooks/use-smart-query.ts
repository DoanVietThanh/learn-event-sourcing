/**
 * Smart Query Hook - Enhanced TanStack Query with Error Handling
 *
 * Query hook that integrates with the centralized error handling system
 * and provides context-aware error management with automatic retry logic.
 */

"use client";

import React from "react";
import {
  useQuery,
  UseQueryOptions,
  QueryKey,
  QueryFunction,
  UseQueryResult,
} from "@tanstack/react-query";
import {
  ErrorHandler,
  ErrorContext,
  ErrorClassifier,
} from "@/lib/error-handling";
import { logger } from "@/lib/utils/logger";

interface SmartQueryOptions<TData, TError = unknown>
  extends Omit<
    UseQueryOptions<TData, TError>,
    "onError" | "queryKey" | "queryFn"
  > {
  context?: ErrorContext;
  showErrorToast?: boolean;
  showErrorInline?: boolean;
  retryOnError?: boolean;
  errorBoundary?: boolean;
  silentError?: boolean;
  autoRetry?: boolean;
  autoRetryDelay?: number;
  maxAutoRetries?: number;
  onError?: (error: TError) => void;
  onRetry?: (failureCount: number, error: TError) => void;
  errorMetadata?: Record<string, unknown>;
}

type SmartQueryResult<TData, TError = unknown> = UseQueryResult<
  TData,
  TError
> & {
  retryWithContext: () => void;
  clearError: () => void;
  isRetrying: boolean;
  retryCount: number;
};

export function useSmartQuery<TData = unknown, TError = unknown>(
  queryKey: QueryKey,
  queryFn: QueryFunction<TData>,
  options: SmartQueryOptions<TData, TError> = {}
): SmartQueryResult<TData, TError> {
  const {
    context = ErrorContext.PAGE_LOAD,
    showErrorToast = false,
    showErrorInline = true,
    retryOnError = true,
    errorBoundary = false,
    silentError = false,
    autoRetry = false,
    autoRetryDelay = 2000,
    maxAutoRetries = 3,
    onError,
    onRetry,
    errorMetadata = {},
    ...queryOptions
  } = options;

  const query = useQuery({
    ...queryOptions,
    queryKey,
    queryFn,
    retry: retryOnError
      ? (failureCount, error) => {
          // Custom retry logic based on error classification
          const appError = ErrorClassifier.classify(error, context);
          const shouldRetry =
            appError.retryable &&
            failureCount < ((queryOptions.retry as number) || 3);

          if (shouldRetry) {
            logger.info("Smart query retry attempt", {
              queryKey: JSON.stringify(queryKey),
              failureCount,
              context,
              errorCode: appError.code,
            });

            onRetry?.(failureCount, error as TError);
          }

          return shouldRetry;
        }
      : false,
    retryDelay: (attemptIndex) => {
      // Exponential backoff with jitter
      const baseDelay = autoRetryDelay;
      const exponentialDelay = Math.min(1000 * 2 ** attemptIndex, 30000);
      const jitter = Math.random() * 1000;
      return exponentialDelay + jitter;
    },
  });

  // Handle errors using useEffect since onError is not available in v5
  React.useEffect(() => {
    if (query.error && !silentError) {
      const appError = ErrorHandler.handle(query.error, context, {
        showToast: showErrorToast,
        metadata: {
          queryKey: JSON.stringify(queryKey),
          component: "useSmartQuery",
          context,
          showErrorInline,
          errorBoundary,
          ...errorMetadata,
        },
      });

      // Log query error
      logger.error("Smart query error", {
        error: query.error,
        errorId: appError.id,
        queryKey: JSON.stringify(queryKey),
        context,
        severity: appError.severity,
        retryable: appError.retryable,
      });

      // Throw error for error boundary if enabled
      if (errorBoundary) {
        throw query.error;
      }

      // Call custom error handler
      onError?.(query.error as TError);
    }
  }, [
    query.error,
    silentError,
    context,
    showErrorToast,
    queryKey,
    showErrorInline,
    errorBoundary,
    errorMetadata,
    onError,
  ]);

  // Handle success using useEffect
  React.useEffect(() => {
    if (query.data && query.isSuccess) {
      // Log successful query
      logger.debug("Smart query success", {
        queryKey: JSON.stringify(queryKey),
        context,
        dataType: typeof query.data,
      });
    }
  }, [query.data, query.isSuccess, queryKey, context]);

  const retryWithContext = () => {
    logger.info("Smart query manual retry triggered", {
      queryKey: JSON.stringify(queryKey),
      context,
    });
    query.refetch();
  };

  const clearError = () => {
    // Clear query error state by refetching
    query.refetch();
  };

  return {
    ...query,
    retryWithContext,
    clearError,
    isRetrying: query.isFetching && !query.isLoading,
    retryCount: query.failureCount || 0,
  };
}

/**
 * Smart Query Hook with Suspense Support
 */
export function useSmartSuspenseQuery<TData = unknown, TError = unknown>(
  queryKey: QueryKey,
  queryFn: QueryFunction<TData>,
  options: SmartQueryOptions<TData, TError> = {}
): SmartQueryResult<TData, TError> {
  return useSmartQuery(queryKey, queryFn, {
    ...options,
    errorBoundary: true, // Force error boundary for suspense queries
  });
}

/**
 * Smart Background Query Hook - For polling and background updates
 */
export function useSmartBackgroundQuery<TData = unknown, TError = unknown>(
  queryKey: QueryKey,
  queryFn: QueryFunction<TData>,
  options: Omit<
    SmartQueryOptions<TData, TError>,
    "context" | "showErrorToast" | "silentError"
  > = {}
): SmartQueryResult<TData, TError> {
  return useSmartQuery(queryKey, queryFn, {
    ...options,
    context: ErrorContext.BACKGROUND,
    showErrorToast: false, // Never show toast for background queries
    silentError: true, // Silent by default for background
    retryOnError: true,
    refetchInterval: options.refetchInterval || 30000, // Default 30s polling
    refetchIntervalInBackground: true,
    refetchOnWindowFocus: false, // Don't refetch on focus for background queries
  });
}

/**
 * Smart Infinite Query Hook
 */
export function useSmartInfiniteQuery<TData = unknown, TError = unknown>(
  queryKey: QueryKey,
  queryFn: QueryFunction<TData>,
  options: SmartQueryOptions<TData, TError> & {
    getNextPageParam?: (lastPage: TData, pages: TData[]) => unknown;
    getPreviousPageParam?: (firstPage: TData, pages: TData[]) => unknown;
  } = {}
) {
  const {
    context = ErrorContext.PAGE_LOAD,
    showErrorToast = false,
    silentError = false,
    onError,
    errorMetadata = {},
    getNextPageParam,
    getPreviousPageParam,
    ...queryOptions
  } = options;

  // Note: This would use useInfiniteQuery from TanStack Query
  // For now, we'll return the basic structure
  const query = useQuery({
    ...queryOptions,
    queryKey,
    queryFn,
  });

  // Handle errors using useEffect
  React.useEffect(() => {
    if (query.error && !silentError) {
      ErrorHandler.handle(query.error, context, {
        showToast: showErrorToast,
        metadata: {
          queryKey: JSON.stringify(queryKey),
          component: "useSmartInfiniteQuery",
          context,
          ...errorMetadata,
        },
      });

      onError?.(query.error as TError);
    }
  }, [
    query.error,
    silentError,
    context,
    showErrorToast,
    queryKey,
    errorMetadata,
    onError,
  ]);

  return {
    ...query,
    hasNextPage: false, // Would be implemented with useInfiniteQuery
    hasPreviousPage: false,
    fetchNextPage: () => Promise.resolve(),
    fetchPreviousPage: () => Promise.resolve(),
    isFetchingNextPage: false,
    isFetchingPreviousPage: false,
  };
}

/**
 * Hook for managing multiple related queries
 */
export function useSmartQueries<TData = unknown>(
  queries: Array<{
    queryKey: QueryKey;
    queryFn: QueryFunction<TData>;
    options?: SmartQueryOptions<TData>;
  }>,
  globalOptions: {
    context?: ErrorContext;
    showErrorToast?: boolean;
    silentError?: boolean;
  } = {}
) {
  const results = queries.map(({ queryKey, queryFn, options = {} }) =>
    useSmartQuery(queryKey, queryFn, {
      ...globalOptions,
      ...options,
    })
  );

  const isLoading = results.some((result) => result.isLoading);
  const isError = results.some((result) => result.isError);
  const errors = results
    .filter((result) => result.error)
    .map((result) => result.error);
  const data = results.map((result) => result.data);

  const retryAll = () => {
    results.forEach((result) => result.retryWithContext());
  };

  const clearAllErrors = () => {
    results.forEach((result) => result.clearError());
  };

  return {
    results,
    isLoading,
    isError,
    errors,
    data,
    retryAll,
    clearAllErrors,
  };
}

/**
 * Hook for dependent queries with error handling
 */
export function useSmartDependentQuery<TData = unknown, TDependency = unknown>(
  dependencyQueryKey: QueryKey,
  dependencyQueryFn: QueryFunction<TDependency>,
  dependentQueryKey: (dependency: TDependency) => QueryKey,
  dependentQueryFn: (dependency: TDependency) => QueryFunction<TData>,
  options: {
    dependencyOptions?: SmartQueryOptions<TDependency>;
    dependentOptions?: SmartQueryOptions<TData>;
    context?: ErrorContext;
  } = {}
) {
  const {
    dependencyOptions = {},
    dependentOptions = {},
    context = ErrorContext.PAGE_LOAD,
  } = options;

  // First query - dependency
  const dependencyQuery = useSmartQuery(dependencyQueryKey, dependencyQueryFn, {
    context,
    ...dependencyOptions,
  });

  // Second query - dependent on first
  const dependentQuery = useSmartQuery(
    dependencyQuery.data
      ? dependentQueryKey(dependencyQuery.data)
      : ["dependent-disabled"],
    dependencyQuery.data
      ? dependentQueryFn(dependencyQuery.data)
      : () => Promise.reject("Dependency not ready"),
    {
      enabled: !!dependencyQuery.data && !dependencyQuery.isError,
      context,
      ...dependentOptions,
    }
  );

  return {
    dependencyQuery,
    dependentQuery,
    isLoading: dependencyQuery.isLoading || dependentQuery.isLoading,
    isError: dependencyQuery.isError || dependentQuery.isError,
    error: dependencyQuery.error || dependentQuery.error,
    data: dependentQuery.data,
    retryAll: () => {
      dependencyQuery.retryWithContext();
      if (dependentQuery.isError) {
        dependentQuery.retryWithContext();
      }
    },
  };
}
