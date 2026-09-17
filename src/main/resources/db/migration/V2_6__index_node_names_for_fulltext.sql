-- Adds one index and changes nothing else. No table is altered otherwise and the combined-objects view is untouched.
--
-- TBL_NODES gains a FULLTEXT index over (NAME, DESCRIPTION). MATCH requires an index whose column list is exactly the
-- one being matched — a partial list is error 1191, not a slower scan — and the existing ft_tbl_nodes_description
-- covers DESCRIPTION alone, which the node search cannot use because it searches the name as well.
--
-- Note what is NOT here. An earlier revision of this migration also concatenated PUBL.XMLTEXT into the view's
-- SEARCH_TEXT so that the combined search could see the digitised page. That column is mapped and selected with every
-- row of /objects, so it would have carried a slice of roughly 65 MB of scanned text onto the heap on every request to
-- the endpoint, searching or not. The body is reached with a correlated subquery instead
-- (CombinedObjectSpecification.matches), which needs no schema change.
--
-- Flyway is off in the real environments, so this has to be applied by hand there before the code that depends on it
-- ships. It is written to survive a re-run.

ALTER TABLE TBL_NODES ADD FULLTEXT INDEX IF NOT EXISTS ft_tbl_nodes_name_description (NAME, DESCRIPTION);
