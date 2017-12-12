create or replace FUNCTION ribalta_rimborsi (p_year integer)
 returns text as $$
declare
	progr integer default 0;
	rec_rimb  record;
 c cursor(p_year integer) for
	SELECT * FROM RIMBORSO_MISSIONE WHERE stato_flusso != 'INV' AND STATO != 'ANN' AND STATO != 'DEF' and anno = p_year ORDER BY ISTITUTO, NUMERO;
begin
 open c (p_year);
 loop
   fetch c into rec_rimb;
   exit when not found;  
	select progressivo_rimborso
	into	progr
	from	dati_istituto
	where	istituto = rec_rimb.uo_spesa and
		anno = p_year + 1;
		
     BEGIN
	update dati_istituto
	set	progressivo_rimborso = progr + 1
	where	istituto = rec_rimb.uo_spesa and
		anno = p_year + 1;
     EXCEPTION
	WHEN OTHERS THEN NULL;
    END;
	update rimborso_missione
	set	anno = p_year + 1, numero = progr + 1
	where	id = rec_rimb.id;
 end loop;
  close c;
  return 'ok';
end; $$

LANGUAGE plpgsql;
