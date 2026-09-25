-- Apply to the existing Supabase project after reviewing current product/RLS schema.
-- Public callers may increment this counter, but cannot update other product fields.
create or replace function public.increment_inquiry_click(p_product_id uuid)
returns bigint
language plpgsql
security definer
set search_path = ''
as $$
declare
    new_count bigint;
begin
    update public.products
    set inquiry_click_count = inquiry_click_count + 1
    where id = p_product_id
    returning inquiry_click_count into new_count;
    if new_count is null then
        raise exception 'Product not found' using errcode = 'P0002';
    end if;
    return new_count;
end;
$$;

revoke all on function public.increment_inquiry_click(uuid) from public;
grant execute on function public.increment_inquiry_click(uuid) to anon, authenticated;
