INSERT INTO `c_configuration` (`name`, `value`, `enabled`,`is_trap_door`,`description`) VALUES ('installment-amount-rounding-mode', '0', 1, 1, '0 - UP, 1 - DOWN, 2- CEILING, 3- FLOOR, 4- HALF_UP, 5- HALF_DOWN, 6 - HALF_EVEN');

update c_configuration cc join c_configuration cc2 on cc.name = 'installment-amount-rounding-mode' and cc2.name = 'rounding-mode'
set cc.value = cc2.value